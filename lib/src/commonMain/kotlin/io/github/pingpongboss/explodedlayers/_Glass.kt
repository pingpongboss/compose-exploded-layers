package io.github.pingpongboss.explodedlayers

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Draws an animated glass background. */
@Composable
internal fun Modifier.glass(
    state: GlassState = rememberGlassState(),
    alpha: Float = 1f,
    isDragging: Boolean = false,
    holes: List<Rect> = emptyList(),
): Modifier {
    val transition = rememberInfiniteTransition(label = "glassSweep")
    val sweepAnim by
        transition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(durationMillis = 400, delayMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "highlightSweep",
        )

    return this.drawBehind {
        drawIntoCanvas { canvas ->
            val outside =
                Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height).inflate(500f))
                    for (hole in holes) {
                        op(this, Path().apply { addRect(hole) }, PathOperation.Difference)
                    }
                }

            canvas.withSave {
                canvas.clipPath(outside)
                drawGlass(state, alpha, sweepAnim, isDragging)
            }
        }
    }
}

private fun DrawScope.drawGlass(
    state: GlassState,
    alpha: Float,
    sweepAnim: Float,
    isDragging: Boolean,
) {
    val cornerRadius = CornerRadius(state.cornerSize.toPx(size, this))

    // 1) base translucent fill
    drawRoundRect(
        color = state.tint.copy(alpha = state.tintAlpha),
        alpha = alpha,
        cornerRadius = cornerRadius,
    )

    // 2) diagonal static specular streak
    val bandWidth = size.width * 0.38f
    val bandStart = Offset(-bandWidth * 0.2f, size.height * -0.1f)
    val bandEnd = Offset(bandWidth, size.height * 0.6f)
    val specular =
        Brush.linearGradient(
            colorStops =
                arrayOf(
                    0.00f to Color.White.copy(alpha = 0.36f),
                    0.35f to Color.White.copy(alpha = 0.16f),
                    0.50f to Color.Transparent,
                    1.00f to Color.Transparent,
                ),
            start = bandStart,
            end = bandEnd,
        )
    drawRoundRect(brush = specular, alpha = alpha, cornerRadius = cornerRadius)

    // 3) animated sweeping highlight**
    val sweepWidth = size.width * 0.5f
    val sweepX = size.width * sweepAnim - sweepWidth
    val sweepBrush =
        Brush.linearGradient(
            colors =
                listOf(
                    Color.Transparent,
                    state.highlight.copy(alpha = 0.25f),
                    state.highlight.copy(alpha = 0.10f),
                    Color.Transparent,
                ),
            start = Offset(sweepX, 0f),
            end = Offset(sweepX + sweepWidth, size.height),
        )
    drawRoundRect(brush = sweepBrush, alpha = alpha, cornerRadius = cornerRadius)

    // 4) beveled rim
    val rim =
        Brush.linearGradient(
            colors =
                listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.12f),
                    Color.Black.copy(alpha = 0.10f),
                    Color.Transparent,
                ),
            start = Offset.Zero,
            end = Offset(size.width, size.height),
        )
    drawRoundRect(
        brush = rim,
        style = Stroke(width = state.rimWidth.toPx()),
        alpha = alpha,
        cornerRadius = cornerRadius,
    )

    // 5) faint inner shadow opposite the highlight
    val innerRim =
        Brush.linearGradient(
            colors = listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent),
            start = Offset(size.width, size.height),
            end = Offset.Zero,
        )
    drawRoundRect(
        brush = innerRim,
        style = Stroke(width = (state.rimWidth.toPx())),
        alpha = alpha,
        cornerRadius = cornerRadius,
    )

    if (isDragging) {
        val margin = 8.dp.toPx()
        // top (up)
        drawArrow(Offset(size.width / 2f, -margin), Offset(0f, -1f))
        // bottom (down)
        drawArrow(Offset(size.width / 2f, size.height + margin), Offset(0f, 1f))
        // left (left)
        drawArrow(Offset(-margin, size.height / 2f), Offset(-1f, 0f))
        // right (right)
        drawArrow(Offset(size.width + margin, size.height / 2f), Offset(1f, 0f))
    }
}

private fun DrawScope.drawArrow(center: Offset, direction: Offset) {
    val len = direction.getDistance()
    if (len == 0f) return
    val dir = direction / len

    val arrowLength = 12.dp.toPx()
    val arrowWidth = 8.dp.toPx()
    val arrowColor = Color.White.copy(alpha = 0.8f)

    val tip = center + dir * (arrowLength / 2f)
    val tail = center - dir * (arrowLength / 2f)
    val ortho = Offset(-dir.y, dir.x) * (arrowWidth / 2f)

    val path =
        Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(tail.x + ortho.x, tail.y + ortho.y)
            lineTo(tail.x - ortho.x, tail.y - ortho.y)
            close()
        }
    drawPath(path, color = arrowColor)
    drawPath(path, color = Color.Black.copy(alpha = 0.25f), style = Fill)
}

/**
 * Creates and remembers a [GlassState] object, which holds the visual configuration for the `glass`
 * modifier. This state can be customized to change the appearance of the glass effect.
 *
 * The parameters are remembered and will not cause recomposition if they change.
 *
 * @param tint The base color of the glass.
 * @param tintAlpha The alpha transparency of the base [tint] color.
 * @param highlight The color of the animated sweeping highlight.
 * @param rimWidth The width of the beveled rim effect.
 * @param cornerSize The rounded corner size for the glass shape.
 * @return A remembered [GlassState] instance.
 */
@Composable
fun rememberGlassState(
    tint: Color = Color.LightGray,
    tintAlpha: Float = .22f,
    highlight: Color = Color.DarkGray,
    rimWidth: Dp = 1.dp,
    cornerSize: CornerSize = ZeroCornerSize,
): GlassState {
    return remember { GlassState(tint, tintAlpha, highlight, rimWidth, cornerSize) }
}

class GlassState
internal constructor(
    val tint: Color,
    val tintAlpha: Float,
    val highlight: Color,
    val rimWidth: Dp,
    val cornerSize: CornerSize,
)
