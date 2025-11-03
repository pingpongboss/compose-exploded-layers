package io.github.pingpongboss.explodedlayers.samples.common.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

@Composable
fun InfiniteAnimationEffect(
    enabled: Boolean,
    animatable: Animatable<Float, AnimationVector1D>,
    onAnimationUpdate: (Float) -> Unit = {},
) {
    LaunchedEffect(enabled, animatable) {
        if (enabled) {
            while (isActive) {
                animatable.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 2000))
                delay(1.seconds)
                animatable.animateTo(
                    targetValue = Float.MIN_VALUE,
                    animationSpec = tween(durationMillis = 2000),
                )
                delay(1.seconds)
            }
        }
    }

    val latestOnUpdate by rememberUpdatedState(onAnimationUpdate)
    LaunchedEffect(enabled, animatable) {
        if (enabled) {
            snapshotFlow { animatable.value }.collect { latestOnUpdate(it) }
        }
    }
}
