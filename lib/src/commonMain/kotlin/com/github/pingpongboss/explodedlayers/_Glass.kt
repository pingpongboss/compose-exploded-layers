package com.github.pingpongboss.explodedlayers

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Draws an animated glass background. */
@Composable
internal fun Modifier.glass(
    alpha: Float = 1f,
    isDragging: Boolean = false,
    tint: Color = Color.LightGray,
    highlight: Color = Color.DarkGray,
    rimWidth: Dp = 1.dp,
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
        // 1) base translucent fill
        drawRect(color = tint.copy(alpha = 0.22f), alpha = alpha)

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
        drawRect(brush = specular, alpha = alpha)

        // 3) animated sweeping highlight**
        val sweepWidth = size.width * 0.5f
        val sweepX = size.width * sweepAnim - sweepWidth
        val sweepBrush =
            Brush.linearGradient(
                colors =
                    listOf(
                        Color.Transparent,
                        highlight.copy(alpha = 0.25f),
                        highlight.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                start = Offset(sweepX, 0f),
                end = Offset(sweepX + sweepWidth, size.height),
            )
        drawRect(brush = sweepBrush, alpha = alpha)

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
        drawRect(brush = rim, style = Stroke(width = rimWidth.toPx()), alpha = alpha)

        // 5) faint inner shadow opposite the highlight
        val innerRim =
            Brush.linearGradient(
                colors = listOf(Color.Black.copy(alpha = 0.10f), Color.Transparent),
                start = Offset(size.width, size.height),
                end = Offset.Zero,
            )
        drawRect(brush = innerRim, style = Stroke(width = (rimWidth.toPx())), alpha = alpha)

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
        androidx.compose.ui.graphics.Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(tail.x + ortho.x, tail.y + ortho.y)
            lineTo(tail.x - ortho.x, tail.y - ortho.y)
            close()
        }
    drawPath(path, color = arrowColor)
    drawPath(path, color = Color.Black.copy(alpha = 0.25f), style = Fill)
}
