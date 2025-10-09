package com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import com.github.pingpongboss.explodedlayers.ExplodedLayersState
import com.github.pingpongboss.explodedlayers.SeparateLayer
import com.github.pingpongboss.explodedlayers.sample.fonts.montserrat
import com.github.pingpongboss.explodedlayers.sample.utils.transformToPressedState
import com.github.pingpongboss.explodedlayers.separateLayer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
actual fun GradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
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
        SeparateLayer({
                          Text(
                              text = label.uppercase(),
                              color = Color.White,
                              fontFamily = montserrat(),
                              fontWeight = FontWeight.Bold,
                          )
                      })
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

        drawIntoCanvas { canvas ->
            val paint =
                Paint().asFrameworkPaint().apply {
                    isAntiAlias = true

                    // Create a linear gradient shader
                    shader =
                        LinearGradient(
                            0f,
                            0f,
                            size.width,
                            size.height,
                            GRADIENT_BUTTON_SHADOW_DIFFUSE_COLORS.map { it.toArgb() }.toIntArray(),
                            null,
                            Shader.TileMode.CLAMP,
                        )

                    // Add blur
                    if (diffuseShadowBlurRadius > 0f) {
                        maskFilter =
                            BlurMaskFilter(diffuseShadowBlurRadius, BlurMaskFilter.Blur.NORMAL)
                    }
                }

            // Draw a round rect using the paint
            val rect = RectF(0f + shadowOffset, 0f, size.width, size.height)
            canvas.nativeCanvas.drawRoundRect(
                rect,
                cornerRadius.x, // corner radius X
                cornerRadius.y, // corner radius Y
                paint,
            )
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
