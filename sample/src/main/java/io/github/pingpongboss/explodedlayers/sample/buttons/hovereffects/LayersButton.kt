package io.github.pingpongboss.explodedlayers.sample.buttons.hovereffects

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.sample.fonts.montserrat
import io.github.pingpongboss.explodedlayers.sample.utils.ComposableModifier.thenIf
import io.github.pingpongboss.explodedlayers.sample.utils.transformToPressedState
import io.github.pingpongboss.explodedlayers.separateLayer
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val LAYER_BUTTON_OUTER_PADDING = 8.dp
private val LAYER_BUTTON_INNER_PADDING = 16.dp
private val LAYER_BUTTON_MIN_WIDTH = 160.dp
private val LAYER_BUTTON_BORDER_WIDTH = 2.dp

private val LAYER_BUTTON_PRESSED_OFFSET = 4.dp

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun LayersButton(
    label: String,
    colors: List<Color>,
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

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        LayersButtonInternal(
            label = label,
            colors = colors,
            pressed = pressed,
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LayersButtonInternal(
    label: String,
    colors: List<Color>,
    pressed: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    require(colors.isNotEmpty())

    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(LAYER_BUTTON_OUTER_PADDING)
                .drawButtonShapes(colors, pressed)
                .clip(shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                )
                .padding(LAYER_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        SeparateLayer({
            Text(
                text = label.uppercase(),
                fontFamily = montserrat,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        })
    }
}

@Composable
private fun Modifier.drawButtonShapes(colors: List<Color>, pressed: Boolean): Modifier {
    val offset by
        animateDpAsState(
            targetValue = if (pressed) LAYER_BUTTON_PRESSED_OFFSET else 0.dp,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
        )

    var modifier = this
    for (i in colors.size - 1 downTo 0) {
        val color = colors[i]
        modifier = modifier.drawButtonShape(color, offset * i).thenIf(i != 0) { separateLayer() }
    }
    return modifier
}

private fun Modifier.drawButtonShape(color: Color, offset: Dp): Modifier {
    return drawBehind {
        val r = size.height / 2f

        // Background
        val backgroundSize = Size(size.width, size.height)
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(r),
            topLeft = Offset(offset.toPx(), offset.toPx()),
            size = backgroundSize,
        )

        // Border
        val strokePx = LAYER_BUTTON_BORDER_WIDTH.toPx()
        val half = strokePx / 2f
        val borderSize = Size(size.width - strokePx, size.height - strokePx)
        val radius = max(0f, r - half)
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(half + offset.toPx(), half + offset.toPx()),
            size = borderSize,
            cornerRadius = CornerRadius(radius),
            style = Stroke(width = strokePx),
        )
    }
}

@Preview
@Composable
fun LayersButtonPreview1() {
    LayersButtonInternal(
        label = "Layers",
        colors = listOf(Color(0xff52b29c), Color(0xFFF5BC45), Color(0xFF6079F6)),
        pressed = false,
        onClick = {},
        interactionSource = remember { MutableInteractionSource() },
    )
}

@Preview
@Composable
fun LayersButtonPreview2() {
    LayersButtonInternal(
        label = "Layers",
        colors = listOf(Color(0xff52b29c), Color(0xFFF5BC45), Color(0xFF6079F6)),
        pressed = true,
        onClick = {},
        interactionSource = remember { MutableInteractionSource() },
    )
}
