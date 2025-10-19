@file:OptIn(ExperimentalExplodedLayersApi::class)

package io.github.pingpongboss.explodedlayers

import androidx.annotation.FloatRange
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import io.github.pingpongboss.explodedlayers.ExplodedLayersDirection.Above
import io.github.pingpongboss.explodedlayers.ExplodedLayersDirection.Behind
import kotlin.math.PI
import kotlin.math.tan

private val LocalState = compositionLocalOf<ExplodedLayersState?> { null }
private val LocalInstanceState = compositionLocalOf<ExplodedLayersInstanceState?> { null }
private val LocalInOverlay = compositionLocalOf { false }

/**
 * Marks a composition as the root container for all [Modifier.separateLayer] and [SeparateLayer]
 * elements declared within it, enabling them to render in distinct visual layers with a pseudo-3D
 * “exploded” perspective.
 *
 * Each nested layer is positioned according to [ExplodedLayersState.offset] and scaled by
 * [ExplodedLayersState.spread], allowing the entire hierarchy to be animated or interactively
 * controlled.
 *
 * This root composable also supports **interactive offset dragging** when
 * [ExplodedLayersState.interactive] is `true`. Users can click-and-drag anywhere within its bounds
 * to dynamically adjust the explosion offset in real time. Dragging is powered by
 * [Modifier.draggable2D][androidx.compose.foundation.gestures.draggable2D] and smoothed by an
 * internal sensitivity constant ([DRAG_SENSITIVITY]).
 *
 * Example usage:
 * ```
 * val state = rememberExplodedLayersState(interactive = true)
 * ExplodedLayersRoot(state) {
 *     Box(
 *         Modifier
 *             .background(Color.Blue) // 1st layer.
 *             .separateLayer()
 *             .background(Color.Red) // 2nd layer.
 *     ) {
 *         SeparateLayer {
 *             Text("Hello world") // 3rd layer.
 *         }
 *     }
 * }
 * ```
 *
 * You can animate or manually adjust the [ExplodedLayersState.spread] property to transition
 * between collapsed and exploded states:
 * ```
 * LaunchedEffect(Unit) {
 *     animate(initialValue = 0f, targetValue = 1f) { value, _ ->
 *         state.spread = value
 *     }
 * }
 * ```
 *
 * @param state The [ExplodedLayersState] controlling interactivity, offset direction, and spread.
 * @param modifier Optional [Modifier] applied to the root container.
 * @param content The composable hierarchy to render with exploded layering.
 * @see rememberExplodedLayersState
 * @see Modifier.separateLayer
 * @see SeparateLayer
 */
@Composable
fun ExplodedLayersRoot(
    state: ExplodedLayersState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    require(LocalState.current == null) { "You must not nest ExplodedLayersRoot() within itself." }

    val instanceState = remember { ExplodedLayersInstanceState() }

    CompositionLocalProvider(
        LocalState provides state,
        LocalInstanceState provides instanceState,
        LocalInOverlay provides false,
    ) {
        val sign = state.direction.sign
        var overlayPosition by remember { mutableStateOf(Offset.Zero) }
        var isDragging by remember { mutableStateOf(false) }

        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect {
                isDragging = it is PressInteraction.Press || it is DragInteraction.Start
            }
        }

        val holes: MutableList<Rect> = mutableStateListOf()

        with(LocalDensity.current) {
            OverlayBox(
                modifier =
                    modifier.skew(state).onGloballyPositioned {
                        overlayPosition = it.positionInWindow()
                    }
            ) {
                base {
                    val offsetX = instanceState.numLayers * state.offset.x * state.spread * sign
                    val offsetY = instanceState.numLayers * state.offset.y * state.spread * sign
                    Box(
                        modifier =
                            Modifier.thenIf(state.showBackground && state.spread > 0f) {
                                    glass(
                                        state = state.glassState,
                                        alpha = state.spread,
                                        isDragging = isDragging,
                                        holes = holes,
                                    )
                                }
                                .thenIf(state.interactive && state.spread > 0f) {
                                    draggableOffset(state, interactionSource)
                                }
                                .safePadding(
                                    start = if (offsetX < 0.dp) abs(offsetX) else 0.dp,
                                    top = if (offsetY < 0.dp) abs(offsetY) else 0.dp,
                                    end = if (offsetX > 0.dp) abs(offsetX) else 0.dp,
                                    bottom = if (offsetY > 0.dp) abs(offsetY) else 0.dp,
                                )
                    ) {
                        // Blocks touches on the underlay by layering under the base content.
                        if (state.spread > 0f) BlockTouches()

                        content()
                    }
                }

                overlay(zIndex = sign.toFloat()) {
                    // [SeparateLayer] compositions are rendered in this top-level overlay to
                    // prevent them from being clipped by their parents.
                    if (state.spread > 0f && instanceState.overlayLayers.isNotEmpty()) {
                        CompositionLocalProvider(LocalInOverlay provides true) {
                            holes.clear()
                            instanceState.overlayLayers.forEachIndexed { i, layer ->
                                val pos = i + 1

                                val layerBounds = layer.windowPosition ?: return@forEachIndexed
                                val layerSize = layer.windowSize?.toSize() ?: return@forEachIndexed

                                val absolutePosition = layerBounds - overlayPosition
                                if (state.direction == Behind) {
                                    holes += Rect(absolutePosition, layerSize)
                                }

                                val offsetX = state.offset.x * state.spread * sign
                                val offsetY = state.offset.y * state.spread * sign
                                Box(
                                    Modifier.graphicsLayer {
                                            val x = layerBounds.x - overlayPosition.x
                                            translationX = x + (offsetX * pos).toPx()

                                            val y = layerBounds.y - overlayPosition.y
                                            translationY = y + (offsetY * pos).toPx()
                                        }
                                        .size(layerSize.toDpSize())
                                        .zIndex(pos.toFloat() * sign)
                                ) {
                                    layer.content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Marks all subsequent nodes as belonging to a separate visual layer.
 *
 * This modifier only has an effect when applied within an [ExplodedLayersRoot]. When the layout is
 * exploded, the separated layer is offset according to the configured [ExplodedLayersState.offset]
 * and [ExplodedLayersState.spread].
 *
 * Rendering note: Because the separation occurs after all preceding modifiers, the new layer may be
 * clipped by any prior [Modifier.clip][androidx.compose.ui.draw.clip]. If a layer boundary must be
 * defined after a clip modifier in the same modifier chain, wrap the subsequent modifiers and
 * content in a nested [Box], and apply [SeparateLayer] to that box instead.
 *
 * @return A [Modifier] that marks subsequent nodes as a separate layer within the current
 *   [ExplodedLayersRoot].
 */
@Composable
fun Modifier.separateLayer(): Modifier {
    val state = LocalState.current ?: return this
    val instanceState = LocalInstanceState.current ?: return this

    LaunchedEffect(Unit) { instanceState.numLayers++ }
    DisposableEffect(Unit) { onDispose { instanceState.numLayers-- } }

    return if (state.spread > 0f) {
        val sign = state.direction.sign
        offset(state.offset.x * state.spread * sign, state.offset.y * state.spread * sign)
    } else {
        this
    }
}

/**
 * Marks the given [content] as belonging to a separate visual layer.
 *
 * This composable only has an effect when placed within an [ExplodedLayersRoot]. When the layout is
 * exploded, the separated layer is offset according to the configured [ExplodedLayersState.offset]
 * and [ExplodedLayersState.spread].
 *
 * Rendering note: When exploded, this layer is rendered in a top-level overlay, allowing it to
 * escape clipping effects applied by ancestor modifiers. Because this layer is drawn independently
 * of its parent hierarchy, its exploded appearance may differ slightly from its collapsed state.
 * This can occur if the content draws beyond its bounds or relies on clipping or transformation
 * behavior inherited from its parents.
 *
 * Since [content] is rendered in a different composition context when exploded, any remembered
 * state that you wish to remain consistent should be hoisted out of the [SeparateLayer].
 *
 * @param content The composable content to render in a separate layer.
 */
@Composable
fun SeparateLayer(content: @Composable () -> Unit) {
    val state = LocalState.current ?: return content()
    val instanceState = LocalInstanceState.current ?: return content()
    val inOverlay = LocalInOverlay.current

    val overlayLayer = remember { OverlayLayer(content) }
    overlayLayer.content = content

    if (!inOverlay) {
        LaunchedEffect(Unit) {
            instanceState.numLayers++
            instanceState.overlayLayers += overlayLayer
        }
        DisposableEffect(Unit) {
            onDispose {
                instanceState.numLayers--
                instanceState.overlayLayers -= overlayLayer
            }
        }
    }

    Box(
        modifier =
            Modifier.thenIf(!inOverlay) {
                    onGloballyPositioned {
                        overlayLayer.windowPosition = it.positionInWindow()
                        overlayLayer.windowSize = it.size
                    }
                }
                .thenIf(state.spread > 0f) { graphicsLayer { alpha = 0f } }
    ) {
        content()

        // Blocks touches on the invisible original by layering over the content.
        if (state.spread > 0f) BlockTouches()
    }
}

@Composable
private fun BoxScope.BlockTouches() {
    Box(
        Modifier.matchParentSize().pointerInput(Unit) {
            awaitPointerEventScope { while (true) awaitPointerEvent() }
        }
    )
}

private fun Modifier.skew(state: ExplodedLayersState): Modifier {
    return if (state.spread > 0f) {
        val halfLifeX = state.initialOffset.x / 1.5f
        val halfLifeY = state.initialOffset.y / 1.5f

        drawWithContent {
            fun tanDeg(d: Float) = tan(toRadians(d.toDouble())).toFloat()

            val normalizeX = normalize(state.offset.x.toPx(), halfLife = halfLifeX.toPx())
            val normalizeY = normalize(state.offset.y.toPx(), halfLife = halfLifeY.toPx())

            val degreesX = 0f
            val degreesY = -10f * state.spread * normalizeX * normalizeY
            val pivot: TransformOrigin = TransformOrigin.Center

            val px = size.width * pivot.pivotFractionX
            val py = size.height * pivot.pivotFractionY

            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(px, py)
                canvas.skew(tanDeg(degreesX), tanDeg(degreesY))
                canvas.translate(-px, -py)

                // draw the original content under the skew
                this@drawWithContent.drawContent()
                canvas.restore()
            }
        }
    } else {
        this
    }
}

/**
 * Creates and remembers an [ExplodedLayersState] to control the visual separation (or “explosion”)
 * of layered composables rendered within [ExplodedLayersRoot].
 *
 * This state defines both the **offset direction** and the **spread amount** used to displace each
 * layer in perspective, as well as whether the root supports **interactive dragging**.
 *
 * When [interactive] is `true`, users can click and drag directly within the [ExplodedLayersRoot]
 * to dynamically adjust the [offset] at runtime, providing an intuitive way to explore different
 * explosion directions. Dragging is enabled through an internal
 * [Modifier.draggable2D][androidx.compose.foundation.gestures.draggable2D] modifier that updates
 * [offset] continuously as the pointer moves.
 *
 * Typical usage:
 * ```
 * val explodedState = rememberExplodedLayersState(interactive = true)
 * ExplodedLayersRoot(state = explodedState) {
 *     // ... layered composables using Modifier.separateLayer() or SeparateLayer()
 * }
 * ```
 *
 * @param interactive Whether the exploded layers can be interactively dragged by the user. If
 *   `true`, [offset] is updated by pointer input gestures inside [ExplodedLayersRoot]. Defaults to
 *   `true`.
 * @param offset The base offset applied between each consecutive layer when fully exploded.
 *   Negative or positive values determine the separation direction along the X and Y axes.
 * @param initialSpread The normalized initial expansion factor of the explosion, from `0f` (fully
 *   collapsed) to `1f` (fully exploded). This defines the starting visual state of the exploded
 *   layers before any animation or user interaction occurs. Defaults to `1f`.
 * @return A remembered [ExplodedLayersState] instance.
 */
@Composable
fun rememberExplodedLayersState(
    interactive: Boolean = true,
    showBackground: Boolean = true,
    offset: DpOffset = ExplodedLayersDefaults.offset(),
    initialDirection: ExplodedLayersDirection = Above,
    @FloatRange(from = 0.0, to = 1.0) initialSpread: Float = 1f,
    glassState: GlassState = rememberGlassState(),
): ExplodedLayersState {
    return remember {
        ExplodedLayersState(
            interactive = interactive,
            showBackground = showBackground,
            initialOffset = offset,
            initialDirection = initialDirection,
            spread = initialSpread,
            glassState = glassState,
        )
    }
}

@RequiresOptIn(
    message = "This API is experimental and may change or be removed in the future.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ExperimentalExplodedLayersApi

/**
 * Defines the direction in which layers are exploded relative to the base content.
 *
 * This determines the z-ordering and visual stacking of separated layers.
 *
 * @see ExplodedLayersState.direction
 */
sealed class ExplodedLayersDirection(internal val sign: Int) {

    /**
     * Layers are exploded above the base content.
     *
     * This is the default direction.
     */
    data object Above : ExplodedLayersDirection(sign = 1)

    /**
     * Layers are exploded below the base content. If `showBackground = true` is passed into
     * [rememberExplodedLayersState], a visual hole will be cut out of the base layer.
     *
     * This currently only supports [SeparateLayer] but not [Modifier.separateLayer].
     */
    @ExperimentalExplodedLayersApi data object Behind : ExplodedLayersDirection(sign = -1)
}

/**
 * Holds and manages configuration for rendering exploded layers within [ExplodedLayersRoot].
 *
 * The [ExplodedLayersState] defines how layers are displaced in 2D perspective space:
 * - [offset] determines the direction and distance between consecutive layers.
 * - [spread] determines the current normalized explosion amount from `0f` (collapsed) to `1f`
 *   (fully exploded).
 *
 * This state can be shared across multiple [ExplodedLayersRoot] compositions to synchronize their
 * visual spread or animation.
 *
 * Example:
 * ```
 * val state = rememberExplodedLayersState()
 * ExplodedLayersRoot(state) {
 *     SeparateLayer { ... }
 *     SeparateLayer { ... }
 * }
 *
 * // Animate the spread
 * LaunchedEffect(Unit) {
 *     animate(
 *         initialValue = 0f,
 *         targetValue = 1f
 *     ) { value, _ -> state.spread = value }
 * }
 * ```
 *
 * @property interactive Whether the exploded layers can be interactively dragged by the user.
 * @property initialOffset The initial offset.
 */
class ExplodedLayersState
internal constructor(
    val interactive: Boolean,
    val showBackground: Boolean,
    val initialOffset: DpOffset,
    initialDirection: ExplodedLayersDirection,
    spread: Float,
    val glassState: GlassState,
) {

    /**
     * The directional offset applied between consecutive layers when [spread] = 1f.
     *
     * Defines the displacement per layer along the X and Y axes, controlling both direction and
     * distance of the exploded view.
     *
     * For example, an offset of `DpOffset(x = -40.dp, y = 40.dp)` moves each layer 40 dp left and
     * 40 dp down from the previous one.
     */
    var offset: DpOffset by mutableStateOf(initialOffset)

    /**
     * The normalized amount of separation between layers, where:
     * - `0f` = fully collapsed (no separation)
     * - `1f` = fully exploded (each layer offset by the full [initialOffset] distance)
     *
     * Intermediate values (e.g., `0.5f`) produce partial separation, enabling smooth animation.
     *
     * Must remain within `[0f, 1f]`.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    @setparam:FloatRange(from = 0.0, to = 1.0)
    var spread: Float by mutableFloatStateOf(spread)

    var direction: ExplodedLayersDirection by mutableStateOf(initialDirection)
}

internal class ExplodedLayersInstanceState internal constructor() {
    internal var numLayers by mutableIntStateOf(0)
    internal val overlayLayers: MutableList<OverlayLayer> = mutableStateListOf()
}

internal class OverlayLayer(content: @Composable () -> Unit) {
    var content by mutableStateOf(content)
    var windowPosition: Offset? by mutableStateOf(null)
    var windowSize: IntSize? by mutableStateOf(null)
}

/** Contains the default values used by [ExplodedLayersRoot] and [rememberExplodedLayersState]. */
object ExplodedLayersDefaults {

    /**
     * Creates an offset that represents the default per-layer displacement for exploded
     * compositions.
     */
    fun offset() = DpOffset(x = -40.dp, y = 40.dp)
}

// java.Math is not available in kmp
private fun toRadians(deg: Double): Double = deg / 180.0 * PI
