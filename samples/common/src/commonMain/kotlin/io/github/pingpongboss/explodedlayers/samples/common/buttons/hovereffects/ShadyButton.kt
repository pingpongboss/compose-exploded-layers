package io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.samples.common.theme.bungeeRegular
import io.github.pingpongboss.explodedlayers.samples.common.utils.transformToPressedState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Duration.Companion.milliseconds

private val SHADY_BUTTON_MIN_WIDTH = 160.dp
private val SHADY_BUTTON_OUTER_PADDING = 8.dp
private val SHADY_BUTTON_INNER_PADDING = 16.dp
private val SHADY_BUTTON_BACKGROUND_COLOR = Color(0xFF6079F6)

private val PRESSED_BORDER_WIDTH = 14.dp

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ShadyButton(
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

    val borderWidth by
        animateDpAsState(
            targetValue = if (pressed) PRESSED_BORDER_WIDTH else 0.dp,
            animationSpec =
                spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        )

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        ShadyButtonInternal(
            label = label,
            onClick = onClick,
            borderWidth = borderWidth,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun ShadyButtonInternal(
    label: String,
    onClick: () -> Unit,
    borderWidth: Dp,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(SHADY_BUTTON_OUTER_PADDING)
                .widthIn(min = SHADY_BUTTON_MIN_WIDTH)
                .clip(shape)
                .background(SHADY_BUTTON_BACKGROUND_COLOR)
                .drawBorder(borderWidth, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClick = onClick,
                )
                .padding(SHADY_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        SeparateLayer {
            Text(
                text = label.uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Thin,
                fontFamily = bungeeRegular(),
            )
        }
    }
}

@Composable
private fun Modifier.drawBorder(borderWidth: Dp, shape: Shape): Modifier {
    if (borderWidth == 0.dp) return this
    return border(width = borderWidth, color = Color.Black, shape = shape)
}

@Preview
@Composable
fun ShadyButtonPreview1() {
    ShadyButtonInternal(label = "Shady", onClick = {}, borderWidth = 0.dp)
}

@Preview
@Composable
fun ShadyButtonPreview2() {
    ShadyButtonInternal(label = "Shady", onClick = {}, borderWidth = PRESSED_BORDER_WIDTH)
}
