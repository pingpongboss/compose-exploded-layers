package io.github.pingpongboss.explodedlayers.samples.android.buttons.hovereffects

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.snap
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
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
import io.github.pingpongboss.explodedlayers.samples.android.fonts.creepster
import io.github.pingpongboss.explodedlayers.samples.android.utils.transformToPressedState
import io.github.pingpongboss.explodedlayers.separateLayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.milliseconds

private val CREEP_BUTTON_OUTER_PADDING = 8.dp
private val CREEP_BUTTON_INNER_PADDING = 16.dp
private val CREEP_BUTTON_BACKGROUND_COLOR = Color(0xFFE36753)
private val CREEP_BUTTON_BEHIND_COLOR = Color.Black
private val CREEP_BUTTON_BORDER_COLOR = Color.Black
private val CREEP_BUTTON_BORDER_WIDTH = 2.dp
private val CREEP_BUTTON_MIN_WIDTH = 160.dp

private const val CREEP_BUTTON_PRESSED_ROTATION = -10f

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CreepButton(
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
        CreepButtonInternal(
            label = label,
            onClick = onClick,
            pressedTransition = pressedTransition,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource,
            explodedLayersState = explodedLayersState,
        )
    }
}

@Composable
fun CreepButtonInternal(
    label: String,
    onClick: () -> Unit,
    pressedTransition: Transition<Boolean>,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    explodedLayersState: ExplodedLayersState? = null,
) {
    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(CREEP_BUTTON_OUTER_PADDING)
                .widthIn(min = CREEP_BUTTON_MIN_WIDTH)
                .drawBehindButton(pressedTransition, explodedLayersState)
                .separateLayer()
                .rotatePivot(pressedTransition)
                .clip(shape = shape)
                .background(CREEP_BUTTON_BACKGROUND_COLOR)
                .border(
                    width = CREEP_BUTTON_BORDER_WIDTH,
                    color = CREEP_BUTTON_BORDER_COLOR,
                    shape = shape,
                )
                .clickable(
                    interactionSource =
                        interactionSource ?: remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClick,
                )
                .padding(CREEP_BUTTON_INNER_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        SeparateLayer{
            Text(
                text = label.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = creepster,
            )
        }
    }
}

private val EYE_OUTER_COLOR = Color(0xFFDAD9D9)
private val EYE_INNER_COLOR = Color.Black

private const val EYE_RADIUS_FRAC = 0.2f
private const val EYE_PUPIL_RADIUS_FRAC = 0.08f

private const val EYE_1_X_FRAC = 0.7f
private const val EYE_2_X_FRAC = 0.85f
private const val EYE_Y_FRAC = 0.7f

private val PRESSED_EYE_PUPIL_OFFSET = (-2).dp

@Composable
private fun Modifier.drawBehindButton(
    pressedTransition: Transition<Boolean>,
    explodedLayersState: ExplodedLayersState?,
): Modifier {
    val pupilOffset by
        pressedTransition.animateOffset(
            transitionSpec = {
                if (false isTransitioningTo true) {
                    spring(dampingRatio = Spring.DampingRatioHighBouncy)
                } else {
                    snap()
                }
            }
        ) { pressed ->
            if (pressed) {
                Offset(0f, 0f)
            } else {
                with(LocalDensity.current) {
                    Offset(PRESSED_EYE_PUPIL_OFFSET.toPx(), PRESSED_EYE_PUPIL_OFFSET.toPx())
                }
            }
        }

    val notPressed = !pressedTransition.currentState && !pressedTransition.isRunning
    val notExploded = explodedLayersState == null || explodedLayersState.spread == 0f
    if (notPressed && notExploded) return this
    return drawBehind {
        drawRoundRect(
            color = CREEP_BUTTON_BEHIND_COLOR,
            cornerRadius = CornerRadius(size.height / 2f),
        )

        // Define common properties for the eyes
        val eyeY = size.height * EYE_Y_FRAC
        val eye1X = size.width * EYE_1_X_FRAC
        val eye2X = size.width * EYE_2_X_FRAC
        val eyeCenter1 = Offset(eye1X, eyeY)
        val eyeCenter2 = Offset(eye2X, eyeY)

        val eyeWhiteColor = EYE_OUTER_COLOR
        val eyeWhiteRadius = size.height * EYE_RADIUS_FRAC

        drawCircle(color = eyeWhiteColor, radius = eyeWhiteRadius, center = eyeCenter1)
        drawCircle(color = eyeWhiteColor, radius = eyeWhiteRadius, center = eyeCenter2)

        val pupilRadius = size.height * EYE_PUPIL_RADIUS_FRAC
        drawCircle(color = EYE_INNER_COLOR, radius = pupilRadius, center = eyeCenter1 + pupilOffset)
        drawCircle(color = EYE_INNER_COLOR, radius = pupilRadius, center = eyeCenter2 + pupilOffset)
    }
}

@Composable
private fun Modifier.rotatePivot(pressedTransition: Transition<Boolean>): Modifier {
    val rotation by
        pressedTransition.animateFloat(
            transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) }
        ) { pressed ->
            if (pressed) CREEP_BUTTON_PRESSED_ROTATION else 0f
        }

    if (rotation == 0f) return this
    return graphicsLayer {
        rotationZ = rotation

        val cornerRadiusPx = size.height * .5f
        transformOrigin =
            TransformOrigin(pivotFractionX = cornerRadiusPx / size.width, pivotFractionY = .5f)
    }
}

@Preview
@Composable
fun CreepButtonPreview1() {
    CreepButtonInternal(label = "Creep", onClick = {}, pressedTransition = updateTransition(false))
}

@Preview
@Composable
fun CreepButtonPreview2() {
    CreepButtonInternal(label = "Creep", onClick = {}, pressedTransition = updateTransition(true))
}
