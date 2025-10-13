package io.github.pingpongboss.explodedlayers.samples.android.buttons.hovereffects

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.samples.android.fonts.montserrat
import io.github.pingpongboss.explodedlayers.samples.android.utils.transformToPressedState
import io.github.pingpongboss.explodedlayers.separateLayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ExciteButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    explodedLayersState: ExplodedLayersState,
) {
    val interactionSource = remember { MutableInteractionSource() }

    var pressed by remember { mutableStateOf(false) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .transformToPressedState(minPressedDuration = 100.milliseconds)
            .collect { pressed = it }
    }

    val pressedTransition = updateTransition(targetState = pressed, label = "pressed")

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        ExciteButtonInternal(
            label = label,
            onClick = onClick,
            pressedTransition = pressedTransition,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            explodedLayersState = explodedLayersState,
        )
    }
}

private val EXCITE_BUTTON_MIN_WIDTH = 140.dp
private val EXCITE_BUTTON_OUTER_PADDING = 8.dp
private val EXCITE_BUTTON_INNER_PADDING = 16.dp
private val EXCITE_BUTTON_BACKGROUND_COLOR = Color(0xFFF5BC46)

@Composable
private fun ExciteButtonInternal(
    label: String,
    onClick: () -> Unit,
    pressedTransition: Transition<Boolean>,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    explodedLayersState: ExplodedLayersState? = null,
) {
    val shape = RoundedCornerShape(percent = 50)

    val backgroundColor by
        pressedTransition.animateColor(label = "backgroundColor") { pressed ->
            if (pressed) EXCITE_BUTTON_BACKGROUND_COLOR.copy(alpha = 0f)
            else EXCITE_BUTTON_BACKGROUND_COLOR
        }
    Box(
        modifier
            .semantics { role = Role.Button }
            .padding(EXCITE_BUTTON_OUTER_PADDING)
            .widthIn(min = EXCITE_BUTTON_MIN_WIDTH)
            .drawSparkles(pressedTransition, explodedLayersState)
            .separateLayer()
            .clip(shape)
            .drawGradientBackground(pressedTransition, explodedLayersState)
            .background(backgroundColor)
            .border(width = 2.dp, shape = shape, color = Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            )
            .padding(EXCITE_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        val fontScale by
            pressedTransition.animateFloat(
                label = "fontSize",
                transitionSpec = { spring(dampingRatio = .3f) },
            ) { pressed ->
                if (pressed) 1f else .8f
            }
        val fontColor by
            pressedTransition.animateColor(label = "fontColor") { pressed ->
                if (pressed) Color.White else Color.Black
            }
        SeparateLayer({
            Text(
                modifier = Modifier.scale(fontScale),
                text = label.uppercase(),
                fontFamily = montserrat,
                color = fontColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        })
    }
}

private const val HIDDEN_SHAPES_SCALE = 5f

@Composable
private fun Modifier.drawSparkles(
    pressedTransition: Transition<Boolean>,
    explodedLayersState: ExplodedLayersState?,
): Modifier {
    val notPressed = !pressedTransition.currentState && !pressedTransition.isRunning
    val notExploded = explodedLayersState == null || explodedLayersState.spread == 0f
    if (notPressed && notExploded) return this

    val hideProgress by
        pressedTransition.animateFloat(
            label = "offsetProgress",
            transitionSpec = { spring(dampingRatio = .4f, stiffness = Spring.StiffnessMediumLow) },
        ) { pressed ->
            if (pressed) 0f else 1f
        }

    return drawBehind {
        // Bottom-left shapes

        val bottomLeftAnimatedOffset = Offset(hideProgress * 80f, hideProgress * -55f)

        val star1Path =
            shapePath(
                    points = 5,
                    innerRadiusRatio = .35f,
                    rotationDegrees = -85f + hideProgress * 100f,
                    size =
                        Size(
                            35f + hideProgress * HIDDEN_SHAPES_SCALE,
                            35f + hideProgress * HIDDEN_SHAPES_SCALE,
                        ),
                )
                .apply { translate(Offset(-40f, size.height) + bottomLeftAnimatedOffset) }
        drawPath(path = star1Path, color = Color(0xFF85611E), style = Fill)

        val square1Path =
            shapePath(
                    points = 4,
                    innerRadiusRatio = .7f,
                    rotationDegrees = -10f + hideProgress * -160f,
                    size =
                        Size(
                            15f + hideProgress * HIDDEN_SHAPES_SCALE,
                            15f + hideProgress * HIDDEN_SHAPES_SCALE,
                        ),
                )
                .apply { translate(Offset(15f, size.height - 10f) + bottomLeftAnimatedOffset) }
        drawPath(path = square1Path, color = Color(0xFFA822F5), style = Fill)

        val circle1Path =
            Path()
                .apply {
                    addOval(
                        Rect(
                            center = Offset.Zero,
                            radius = 10f + hideProgress * HIDDEN_SHAPES_SCALE,
                        )
                    )
                }
                .apply { translate(Offset(-37f, size.height - 50f) + bottomLeftAnimatedOffset) }
        drawPath(path = circle1Path, color = Color(0xFF3D8972), style = Fill)

        // Top-right shapes

        val topRightAnimatedOffset = Offset(hideProgress * -80f, hideProgress * 55f)

        val triangle2Path =
            shapePath(
                    points = 3,
                    innerRadiusRatio = .5f,
                    rotationDegrees = -75f + hideProgress * -60f,
                    size =
                        Size(
                            35f + hideProgress * HIDDEN_SHAPES_SCALE,
                            35f + hideProgress * HIDDEN_SHAPES_SCALE,
                        ),
                )
                .apply { translate(Offset(size.width + 30f, -15f) + topRightAnimatedOffset) }
        drawPath(path = triangle2Path, color = Color(0xFFA822F5), style = Fill)

        val square2Path =
            shapePath(
                    points = 4,
                    innerRadiusRatio = .7f,
                    rotationDegrees = 20f + hideProgress * 100f,
                    size =
                        Size(
                            20f + hideProgress * HIDDEN_SHAPES_SCALE,
                            20f + hideProgress * HIDDEN_SHAPES_SCALE,
                        ),
                )
                .apply { translate(Offset(size.width - 20f, -10f) + topRightAnimatedOffset) }
        drawPath(path = square2Path, color = Color(0xFFF4B941), style = Fill)

        val circle2Path =
            Path()
                .apply {
                    addOval(
                        Rect(
                            center = Offset.Zero,
                            radius = 10f + hideProgress * HIDDEN_SHAPES_SCALE,
                        )
                    )
                }
                .apply { translate(Offset(size.width + 30f, 60f) + topRightAnimatedOffset) }
        drawPath(path = circle2Path, color = Color(0xFF3D8972), style = Fill)
    }
}

private fun shapePath(
    points: Int,
    innerRadiusRatio: Float,
    rotationDegrees: Float,
    size: Size,
): Path {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val outerRadius = min(w, h) / 2f
    val innerRadius = outerRadius * innerRadiusRatio

    val path = Path().apply { fillType = PathFillType.EvenOdd }

    // Build shape path: alternate outer and inner points
    val step = (2.0 * PI / (points * 2)).toFloat() // angle between adjacent outer/inner in radians
    val startAngle = Math.toRadians(rotationDegrees.toDouble()).toFloat()

    for (i in 0 until points * 2) {
        val isOuter = i % 2 == 0
        val r = if (isOuter) outerRadius else innerRadius
        val angle = startAngle + i * step
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    return path
}

private val PRESSED_BACKGROUND_COLORS =
    arrayOf(
        0f to Color(0xFF75B754),
        .3f to Color(0xFF75B754),
        .47f to Color(0xFFD980CB),
        .53f to Color(0xFFD980CB),
        .7f to Color(0xFFEBAB42),
        1f to Color(0xFFEBAB42),
    )

private fun Modifier.drawGradientBackground(
    pressedTransition: Transition<Boolean>,
    explodedLayersState: ExplodedLayersState?,
): Modifier {
    val notPressed = !pressedTransition.currentState && !pressedTransition.isRunning
    val notExploded = explodedLayersState == null || explodedLayersState.spread == 0f
    if (notPressed && notExploded) return this
    return drawBehind {
        val gradientBrush =
            Brush.linearGradient(
                colorStops = PRESSED_BACKGROUND_COLORS,
                start = Offset(0f, -size.height / 2f),
                end = Offset(size.width, size.height * (3 / 2f)),
            )

        drawRect(brush = gradientBrush)
    }
}

@Preview
@Composable
private fun ExciteButtonPreview1() {
    ExciteButtonInternal(
        label = "Excite",
        onClick = {},
        pressedTransition = updateTransition(false, "pressed"),
    )
}

@Preview
@Composable
private fun ExciteButtonPreview2() {
    ExciteButtonInternal(
        label = "Excite",
        onClick = {},
        pressedTransition = updateTransition(true, "pressed"),
    )
}
