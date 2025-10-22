package io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.samples.common.platform.applyBlur
import io.github.pingpongboss.explodedlayers.samples.common.theme.montserratRegular
import io.github.pingpongboss.explodedlayers.samples.common.utils.transformToPressedState
import io.github.pingpongboss.explodedlayers.separateLayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun GradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    explodedLayersState: ExplodedLayersState,
) {
    val interactionSource = remember { MutableInteractionSource() }

    var pressed by remember { mutableStateOf(false) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .transformToPressedState(minPressedDuration = 250.milliseconds)
            .collect { pressed = it }
    }

    val pressedTransition = updateTransition(pressed)

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        GradientButtonInternal(
            label = label,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            pressedTransition = pressedTransition,
            explodedLayersState = explodedLayersState,
        )
    }
}

private val GRADIENT_BUTTON_OUTER_PADDING = 8.dp
private val GRADIENT_BUTTON_INNER_PADDING = 16.dp
private val GRADIENT_BUTTON_MIN_WIDTH = 140.dp

@Composable
fun GradientButtonInternal(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    pressedTransition: Transition<Boolean>,
    explodedLayersState: ExplodedLayersState? = null,
) {
    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(GRADIENT_BUTTON_OUTER_PADDING)
                .widthIn(min = GRADIENT_BUTTON_MIN_WIDTH)
                .drawShadow(pressedTransition, explodedLayersState)
                .separateLayer()
                .animateButton(pressedTransition)
                .clip(shape)
                .background(Color.Black)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                )
                .padding(GRADIENT_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        SeparateLayer {
            Text(
                text = label.uppercase(),
                color = Color.White,
                fontFamily = montserratRegular(),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private val GRADIENT_BUTTON_SHADOW_PRIMARY_COLOR = Color(0xFFBAE56F)
private val GRADIENT_BUTTON_SHADOW_DIFFUSE_COLORS = listOf(Color(0xFFF8DD61), Color(0xFFC953DE))

private const val PRESSED_BUTTON_AND_SHADOW_OFFSET = 10f
private const val PRESSED_DIFFUSE_SHADOW_BLUR_RADIUS = 25f

private const val PRESSED_ANIMATION_DURATION_TOTAL_MS = 300
private const val PRESSED_ANIMATION_DELAY_MS = 150

@Composable
private fun Modifier.drawShadow(
    pressedTransition: Transition<Boolean>,
    explodedLayersState: ExplodedLayersState?,
): Modifier {
    val delayedTransitionSpec:
        @Composable
        Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> =
        {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis =
                        PRESSED_ANIMATION_DURATION_TOTAL_MS - PRESSED_ANIMATION_DELAY_MS,
                    delayMillis = PRESSED_ANIMATION_DELAY_MS,
                )
            } else {
                tween(
                    durationMillis =
                        PRESSED_ANIMATION_DURATION_TOTAL_MS - PRESSED_ANIMATION_DELAY_MS,
                    delayMillis = 0,
                )
            }
        }

    val shadowOffset by
        pressedTransition.animateFloat(
            transitionSpec = { tween(durationMillis = PRESSED_ANIMATION_DURATION_TOTAL_MS) }
        ) { pressed ->
            if (pressed) PRESSED_BUTTON_AND_SHADOW_OFFSET else 0f
        }
    val primaryShadowAlpha by
        pressedTransition.animateFloat(transitionSpec = delayedTransitionSpec) { pressed ->
            if (pressed) 0f else 1f
        }
    val diffuseShadowBlurRadius by
        pressedTransition.animateFloat(transitionSpec = delayedTransitionSpec) { pressed ->
            if (pressed) PRESSED_DIFFUSE_SHADOW_BLUR_RADIUS else 0f
        }

    val notPressed = !pressedTransition.currentState && !pressedTransition.isRunning
    val notExploded = explodedLayersState == null || explodedLayersState.spread == 0f
    if (notPressed && notExploded) return this
    return drawBehind {
        val cornerRadius = CornerRadius(size.height / 2f)

        // Only draw the diffuse gradient shadow when blur would be active
        if (diffuseShadowBlurRadius > 0f) {
            drawIntoCanvas { canvas ->
                val paint =
                    Paint().apply {
                        isAntiAlias = true

                        // Create a linear gradient shader
                        shader =
                            LinearGradientShader(
                                from = Offset.Zero,
                                to = Offset(size.width, size.height),
                                colors = GRADIENT_BUTTON_SHADOW_DIFFUSE_COLORS,
                                colorStops = null,
                            )

                        applyBlur(this, diffuseShadowBlurRadius)
                    }

                // Draw a round rect using correct parameters
                canvas.drawRoundRect(
                    left = 0f + shadowOffset,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = cornerRadius.x,
                    radiusY = cornerRadius.y,
                    paint = paint,
                )
            }
        }

        drawRoundRect(
            color = GRADIENT_BUTTON_SHADOW_PRIMARY_COLOR.copy(alpha = primaryShadowAlpha),
            cornerRadius = cornerRadius,
            topLeft = Offset(x = shadowOffset, y = 0f),
        )
    }
}

@Composable
private fun Modifier.animateButton(pressedTransition: Transition<Boolean>): Modifier {
    val translation by
        pressedTransition.animateFloat({
            tween(durationMillis = PRESSED_ANIMATION_DURATION_TOTAL_MS)
        }) { pressed ->
            if (pressed) {
                -PRESSED_BUTTON_AND_SHADOW_OFFSET
            } else 0f
        }

    if (!pressedTransition.currentState && !pressedTransition.isRunning) return this
    return graphicsLayer {
        translationX = translation
        translationY = translation
    }
}

@Preview
@Composable
fun GradientButtonPreview1() {
    GradientButtonInternal(
        label = "Gradient",
        onClick = {},
        pressedTransition = updateTransition(false),
    )
}

@Preview
@Composable
fun GradientButtonPreview2() {
    GradientButtonInternal(
        label = "Gradient",
        onClick = {},
        pressedTransition = updateTransition(true),
    )
}
