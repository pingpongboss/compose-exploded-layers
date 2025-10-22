package io.github.pingpongboss.explodedlayers.samples.common.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val MIN_SLIDER_VALUE = 1f / Float.MAX_VALUE

@Composable
fun InfiniteAnimationEffect(
    enabled: Boolean,
    animatable: Animatable<Float, AnimationVector1D>,
    onAnimationUpdate: (Float) -> Unit = {},
) {
    LaunchedEffect(enabled) {
        if (enabled) {
            launch {
                while (true) {
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2000),
                    )
                    delay(1.seconds)
                    animatable.animateTo(
                        targetValue = MIN_SLIDER_VALUE,
                        animationSpec = tween(durationMillis = 2000),
                    )
                    delay(1.seconds)
                }
            }
        }
    }
    if (enabled) {
        onAnimationUpdate(animatable.value)
    }
}
