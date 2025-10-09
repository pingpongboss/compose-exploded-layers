package com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import com.github.pingpongboss.explodedlayers.ExplodedLayersState
import com.github.pingpongboss.explodedlayers.SeparateLayer
import com.github.pingpongboss.explodedlayers.sample.fonts.leagueSpartan
import com.github.pingpongboss.explodedlayers.sample.utils.transformToPressedState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ExpandButton(
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

    val pressedTransition = updateTransition(pressed)

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        ExpandButtonInternal(
            label = label,
            onClick = onClick,
            pressedTransition = pressedTransition,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
        )
    }
}

private val EXPAND_BUTTON_OUTER_PADDING = 8.dp
private val EXPAND_BUTTON_INNER_PADDING = 16.dp
private val EXPAND_BUTTON_MIN_WIDTH = 140.dp

@Composable
fun ExpandButtonInternal(
    label: String,
    onClick: () -> Unit,
    pressedTransition: Transition<Boolean>,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(EXPAND_BUTTON_OUTER_PADDING)
                .widthIn(min = EXPAND_BUTTON_MIN_WIDTH)
                .animateWidth(pressedTransition)
                .clip(shape)
                .drawGradientBackground()
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                )
                .padding(EXPAND_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        SeparateLayer({
            Text(
                text = label.uppercase(),
                fontFamily = leagueSpartan(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color.White,
            )
        })
    }
}

private val PRESSED_ADDITIONAL_WIDTH = 10.dp

@Composable
private fun Modifier.animateWidth(pressedTransition: Transition<Boolean>): Modifier {
    val additionalPadding by
    pressedTransition.animateDp(transitionSpec = { spring(dampingRatio = .3f) }) { pressed ->
        if (pressed) PRESSED_ADDITIONAL_WIDTH else 0.dp
    }

    if (additionalPadding == 0.dp) return this
    return layout { measurable, oldConstraints ->
        // Measure child with more width.
        val newConstraints =
            oldConstraints.copy(
                minWidth = (oldConstraints.minWidth + additionalPadding.roundToPx()),
                maxWidth = (oldConstraints.maxWidth + additionalPadding.roundToPx()),
            )
        val placeable = measurable.measure(newConstraints)

        // Gives ourselves the same width.
        val width = placeable.width.coerceIn(oldConstraints.minWidth, oldConstraints.maxWidth)
        val height = placeable.height.coerceIn(oldConstraints.minHeight, oldConstraints.maxHeight)

        layout(width, height) {
            // Place child at the left edge, so the additional width hangs to the right
            placeable.placeRelative(0, 0)
        }
    }
}

val EXPAND_BUTTON_BACKGROUND_COLOR_1 = Color(0xFF5F79F5)
val EXPAND_BUTTON_BACKGROUND_COLOR_2 = Color(0xFF9833D7)
val EXPAND_BUTTON_BACKGROUND_COLOR_3 = Color(0xFF75B754)

private fun Modifier.drawGradientBackground(): Modifier {
    return background(EXPAND_BUTTON_BACKGROUND_COLOR_1)
        .drawBehind {
            val gradientBrush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            EXPAND_BUTTON_BACKGROUND_COLOR_2,
                            EXPAND_BUTTON_BACKGROUND_COLOR_2.copy(alpha = 0f),
                        ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                )

            drawRect(brush = gradientBrush)
        }
        .drawBehind {
            val gradientBrush =
                Brush.linearGradient(
                    colors =
                        listOf(
                            EXPAND_BUTTON_BACKGROUND_COLOR_3,
                            EXPAND_BUTTON_BACKGROUND_COLOR_3.copy(alpha = 0f),
                        ),
                    start = Offset(0f, size.height),
                    end = Offset(100f, 0f),
                )

            drawRect(brush = gradientBrush)
        }
}

@Preview
@Composable
fun ExpandButtonPreview1() {
    ExpandButtonInternal(
        label = "Expand",
        onClick = {},
        pressedTransition = updateTransition(false),
    )
}

@Preview
@Composable
fun ExpandButtonPreview2() {
    ExpandButtonInternal(label = "Expand", onClick = {}, pressedTransition = updateTransition(true))
}
