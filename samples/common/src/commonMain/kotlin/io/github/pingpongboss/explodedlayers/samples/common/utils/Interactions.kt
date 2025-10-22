package io.github.pingpongboss.explodedlayers.samples.common.utils

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration

fun Interaction.isPressed(): Boolean? {
    return when (this) {
        is PressInteraction.Press -> true
        is PressInteraction -> false
        else -> null
    }
}

/**
 * Transforms the flow of interactions to a flow of pressed states. Ensures that the unpressed state
 * is not emitted until at least [minPressedDuration] has elapsed since the last pressed state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun Flow<Interaction>.transformToPressedState(minPressedDuration: Duration): Flow<Boolean> {
    var lastPressedTime = 0L
    return mapNotNull { it.isPressed() }
        .flatMapLatest { pressed ->
            val now = currentTimeMillis()
            if (pressed) lastPressedTime = now
            flowOf(pressed).onStart {
                if (!pressed) {
                    val elapsed = now - lastPressedTime
                    delay(minPressedDuration.inWholeMilliseconds - elapsed)
                }
            }
        }
}
