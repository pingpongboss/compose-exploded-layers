package io.github.pingpongboss.explodedlayers

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset

private const val DRAG_SENSITIVITY = .4f

@Composable
internal fun Modifier.draggableOffset(
    state: ExplodedLayersState,
    interactionSource: MutableInteractionSource? = null,
): Modifier {
    val density = LocalDensity.current
    val maxOffsetX = abs(state.initialOffset.x) * 2f
    val maxOffsetY = abs(state.initialOffset.y) * 2f

    return draggable2D(
            state =
                rememberDraggable2DState { dragOffset ->
                    with(density) {
                        val newOffsetX = state.offset.x + dragOffset.x.toDp() * DRAG_SENSITIVITY
                        val newOffsetY = state.offset.y + dragOffset.y.toDp() * DRAG_SENSITIVITY
                        state.offset =
                            DpOffset(
                                x = newOffsetX.coerceIn(-maxOffsetX, maxOffsetX),
                                y = newOffsetY.coerceIn(-maxOffsetY, maxOffsetY),
                            )
                    }
                },
            interactionSource = interactionSource,
        )
        .pointerInput(interactionSource) {
            awaitEachGesture {
                // Observe the press; don't require it to be unconsumed
                val down = awaitFirstDown()

                val press = PressInteraction.Press(down.position)
                interactionSource?.tryEmit(press)

                // Wait for the up event (or cancellation)
                val up = waitForUpOrCancellation()
                val end =
                    if (up != null) {
                        PressInteraction.Release(press)
                    } else {
                        PressInteraction.Cancel(press)
                    }
                interactionSource?.tryEmit(end)
            }
        }
}
