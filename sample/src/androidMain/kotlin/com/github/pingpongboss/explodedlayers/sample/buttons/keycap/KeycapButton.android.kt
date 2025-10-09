package com.github.pingpongboss.explodedlayers.sample.buttons.keycap

import android.graphics.BlurMaskFilter
import android.graphics.Matrix
import android.graphics.Shader
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import com.github.pingpongboss.explodedlayers.ExplodedLayersState
import com.github.pingpongboss.explodedlayers.SeparateLayer
import com.github.pingpongboss.explodedlayers.sample.fonts.pixelifySans
import com.github.pingpongboss.explodedlayers.sample.utils.transformToPressedState
import com.github.pingpongboss.explodedlayers.separateLayer
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi

private val GLASS_BUTTON_OUTER_PADDING = 8.dp
private val GLASS_BUTTON_INNER_PADDING = 6.dp
private const val GLASS_BUTTON_BACKGROUND_ALPHA = 0.6f
private const val GLASS_BUTTON_BORDER_ALPHA = 0.7f
private val GLASS_BUTTON_BORDER_RADIUS = 12.dp
private const val GLASS_BUTTON_BORDER_COLOR_STOP = 0.02f

private const val PRESSED_KEYCAP_SCALE = .95f

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
actual fun KeycapButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    explodedLayersState: ExplodedLayersState,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var keycapScale by remember { mutableFloatStateOf(1f) }
    var animatedGlowOuterShrinkOffset by remember { mutableStateOf(DpOffset.Zero) }
    var blurRadiusScale by remember { mutableFloatStateOf(1f) }
    var alphaMultiplier by remember { mutableFloatStateOf(1f) }
    var drawOuterShadow by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions
            .transformToPressedState(minPressedDuration = 50.milliseconds)
            .collect {
                when (it) {
                    true -> {
                        keycapScale = PRESSED_KEYCAP_SCALE
                        animatedGlowOuterShrinkOffset = PRESSED_ANIMATED_GLOW_OUTER_SHRINK_OFFSET
                        blurRadiusScale = PRESSED_ANIMATED_GLOW_BLUR_RADIUS_SCALE
                        alphaMultiplier = PRESSED_ANIMATED_GLOW_ALPHA_MULTIPLIER
                        drawOuterShadow = true
                    }

                    else -> {
                        keycapScale = 1f
                        animatedGlowOuterShrinkOffset = DpOffset.Zero
                        blurRadiusScale = 1f
                        alphaMultiplier = 1f
                        drawOuterShadow = false
                    }
                }
            }
    }

    ExplodedLayersRoot(state = explodedLayersState, modifier = modifier) {
        KeycapButtonInternal(
            label = label,
            onClick = onClick,
            interactionSource = interactionSource,
            keycapScale = keycapScale,
            animatedGlowOuterShrinkOffset = animatedGlowOuterShrinkOffset,
            blurRadiusScale = blurRadiusScale,
            alphaMultiplier = alphaMultiplier,
            drawOuterShadow = drawOuterShadow,
        )
    }
}

@Composable
private fun KeycapButtonInternal(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    keycapScale: Float,
    animatedGlowOuterShrinkOffset: DpOffset,
    blurRadiusScale: Float,
    alphaMultiplier: Float,
    drawOuterShadow: Boolean,
) {
    val shape = RoundedCornerShape(GLASS_BUTTON_BORDER_RADIUS)

    // Glass layer is the button.
    Box(
        modifier =
            modifier
                .semantics { role = Role.Button }
                .padding(GLASS_BUTTON_OUTER_PADDING)
                .drawOuterShadow(
                    enabled = drawOuterShadow,
                    cornerRadius = GLASS_BUTTON_BORDER_RADIUS,
                )
                .separateLayer()
                .drawAnimatedGlow(
                    borderRadius = GLASS_BUTTON_BORDER_RADIUS,
                    outerShrinkOffset = animatedGlowOuterShrinkOffset,
                    blurRadiusScale = blurRadiusScale,
                    alphaMultiplier = alphaMultiplier,
                )
                .separateLayer()
                .clip(shape)
                .background(Color.White.copy(alpha = GLASS_BUTTON_BACKGROUND_ALPHA))
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.linearGradient(
                            0f to Color.Gray.copy(alpha = GLASS_BUTTON_BORDER_ALPHA),
                            GLASS_BUTTON_BORDER_COLOR_STOP to
                                Color.White.copy(alpha = GLASS_BUTTON_BORDER_ALPHA),
                            1 - GLASS_BUTTON_BORDER_COLOR_STOP to
                                Color.White.copy(alpha = GLASS_BUTTON_BORDER_ALPHA),
                            1f to Color.Gray.copy(alpha = GLASS_BUTTON_BORDER_ALPHA),
                            start = Offset.Zero,
                            end = Offset.Infinite.copy(y = 0f),
                        ),
                    ),
                    shape = shape,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
                .padding(GLASS_BUTTON_INNER_PADDING)
    ) {
        SeparateLayer {
            Keycap(modifier = Modifier.scale(keycapScale)) {
                Text(
                    text = label,
                    modifier = Modifier,
                    fontFamily = pixelifySans(),
                    color = Color.White,
                    fontSize = 20.sp,
                )
            }
        }
    }
}

private val KEYCAP_COLOR_LIGHT = Color(0xFF484848)
private val KEYCAP_COLOR_DARK = Color(0xFF2E2E2E)

private val KEYCAP_BACKGROUND_BORDER_RADIUS = 8.dp
private val KEYCAP_BACKGROUND_ELEVATION = 16.dp
private val KEYCAP_BACKGROUND_PADDING = 8.dp

private val KEYCAP_DIVOT_HORIZONTAL_PADDING = 14.dp
private val KEYCAP_DIVOT_VERTICAL_PADDING = 8.dp

@Composable
private fun Keycap(modifier: Modifier, content: @Composable () -> Unit) {
    // Background layer.
    val shape = RoundedCornerShape(KEYCAP_BACKGROUND_BORDER_RADIUS)
    Box(
        modifier =
            modifier
                .surface(
                    shape = shape,
                    backgroundBrush =
                        verticalGradientBrush(
                            topColor = KEYCAP_COLOR_LIGHT,
                            bottomColor = KEYCAP_COLOR_DARK,
                        ),
                    border = null,
                    shadowElevation =
                        with(LocalDensity.current) { KEYCAP_BACKGROUND_ELEVATION.toPx() },
                )
                .padding(KEYCAP_BACKGROUND_PADDING)
    ) {
        // Divot layer.
        val divotShape = RoundedCornerShape(percent = 50)
        SeparateLayer {
            Box(
                modifier =
                    Modifier.surface(
                            shape = divotShape,
                            backgroundBrush =
                                verticalGradientBrush(
                                    topColor = KEYCAP_COLOR_DARK,
                                    bottomColor = KEYCAP_COLOR_LIGHT,
                                ),
                            border = null,
                            shadowElevation = 0f,
                        )
                        .padding(
                            horizontal = KEYCAP_DIVOT_HORIZONTAL_PADDING,
                            vertical = KEYCAP_DIVOT_VERTICAL_PADDING,
                        )
            ) {
                content()
            }
        }
    }
}

@Composable
private fun verticalGradientBrush(topColor: Color, bottomColor: Color): Brush =
    Brush.linearGradient(
        0f to topColor,
        1f to bottomColor,
        start = Offset.Zero,
        end = Offset.Infinite.copy(x = 0f),
    )

@Stable
private fun Modifier.surface(
    shape: Shape,
    backgroundBrush: Brush,
    border: BorderStroke?,
    shadowElevation: Float,
) =
    this.then(
            if (shadowElevation > 0f) {
                Modifier.graphicsLayer(
                    shadowElevation = shadowElevation,
                    shape = shape,
                    clip = false,
                )
            } else {
                Modifier
            }
        )
        .then(if (border != null) Modifier.border(border, shape) else Modifier)
        .background(brush = backgroundBrush, shape = shape)
        .clip(shape)

private const val ANIMATED_GLOW_COLOR_ALPHA = 0.4f
private const val ANIMATED_GLOW_BLUR_RADIUS = 30f
private const val ANIMATED_GLOW_OUTER_OFFSET = 5f
private const val PRESSED_ANIMATED_GLOW_BLUR_RADIUS_SCALE = .2f
private const val PRESSED_ANIMATED_GLOW_ALPHA_MULTIPLIER = 2f
private val PRESSED_ANIMATED_GLOW_OUTER_SHRINK_OFFSET = DpOffset(x = 10.dp, y = 7.dp)

@Composable
private fun Modifier.drawAnimatedGlow(
    borderRadius: Dp,
    outerShrinkOffset: DpOffset,
    blurRadiusScale: Float,
    alphaMultiplier: Float,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(animation = tween(durationMillis = 4000, easing = LinearEasing)),
        )

    val animatedGlowColors =
        listOf(
            Color(0xFF00FFD1).copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // aqua
            Color.Yellow.copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // yellow
            Color.Red.copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // red
            Color.Blue.copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // blue
            Color(0xFF7F00FF).copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // violet
            Color(0xFF00FFD1).copy(alpha = ANIMATED_GLOW_COLOR_ALPHA * alphaMultiplier), // aqua
        )

    return drawBehind {
        drawIntoCanvas { canvas ->
            val paint =
                Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.FILL
                    shader =
                        SweepGradientShader(
                            center = Offset(size.width / 2f, size.height / 2f),
                            colors = animatedGlowColors,
                            colorStops = null,
                        )

                    val matrix =
                        Matrix().apply {
                            // Rotate first
                            setRotate(rotation, center.x, center.y)
                            // Stretch horizontally (or vertically) to match button aspect ratio
                            val scaleX = size.width / size.height
                            val scaleY = 1f
                            postScale(scaleX, scaleY, center.x, center.y)
                        }
                    (shader as Shader).setLocalMatrix(matrix)

                    maskFilter =
                        BlurMaskFilter(
                            ANIMATED_GLOW_BLUR_RADIUS * blurRadiusScale,
                            BlurMaskFilter.Blur.NORMAL,
                        )
                }

            canvas.drawRoundRect(
                left = 0f - ANIMATED_GLOW_OUTER_OFFSET + outerShrinkOffset.x.toPx(),
                top = 0f - ANIMATED_GLOW_OUTER_OFFSET + outerShrinkOffset.y.toPx(),
                right = size.width + ANIMATED_GLOW_OUTER_OFFSET - outerShrinkOffset.x.toPx(),
                bottom = size.height + ANIMATED_GLOW_OUTER_OFFSET - outerShrinkOffset.y.toPx(),
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint = Paint().apply { asFrameworkPaint().set(paint) },
            )
        }
    }
}

private val OUTER_SHADOW_ELEVATION = 2.dp

@Composable
private fun Modifier.drawOuterShadow(enabled: Boolean, cornerRadius: Dp): Modifier {
    if (!enabled) return this
    return drawBehind {
        val w = size.width
        val h = size.height

        val shapePath =
            Path().apply {
                addRoundRect(RoundRect(0f, 0f, w, h, cornerRadius.toPx(), cornerRadius.toPx()))
            }

        val shadowPath =
            shapePath.copy().apply {
                val matrix = Matrix()
                matrix.setScale(1.01f, 1.0f, w / 2f, 0f)
                matrix.postTranslate(0f, OUTER_SHADOW_ELEVATION.toPx())

                val androidPath = asAndroidPath()
                androidPath.transform(matrix)
            }

        val maskedPath = Path().apply { op(shadowPath, shapePath, PathOperation.Difference) }

        drawIntoCanvas { canvas ->
            val paint =
                Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        4f, // blur radius
                        0f, // offsetX
                        0f, // offsetY
                        Color.Black.copy(alpha = .2f).toArgb(),
                    )
                }
            canvas.nativeCanvas.drawPath(maskedPath.asAndroidPath(), paint)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KeycapButtonPreview() {
    Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        val interactionSource = remember { MutableInteractionSource() }
        KeycapButtonInternal(
            label = "+ Add to cart",
            onClick = { Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show() },
            interactionSource = interactionSource,
            keycapScale = 1f,
            animatedGlowOuterShrinkOffset = DpOffset.Zero,
            blurRadiusScale = 1f,
            alphaMultiplier = 1f,
            drawOuterShadow = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeycapButtonPreviewPressed() {
    Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
        val context = LocalContext.current
        val interactionSource = remember { MutableInteractionSource() }
        KeycapButtonInternal(
            label = "+ Add to cart",
            onClick = { Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show() },
            interactionSource = interactionSource,
            keycapScale = PRESSED_KEYCAP_SCALE,
            animatedGlowOuterShrinkOffset = PRESSED_ANIMATED_GLOW_OUTER_SHRINK_OFFSET,
            blurRadiusScale = PRESSED_ANIMATED_GLOW_BLUR_RADIUS_SCALE,
            alphaMultiplier = PRESSED_ANIMATED_GLOW_ALPHA_MULTIPLIER,
            drawOuterShadow = true,
        )
    }
}
