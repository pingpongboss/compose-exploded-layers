package io.github.pingpongboss.explodedlayers.samples.common

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExperimentalExplodedLayersApi
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersZOrder.Behind
import io.github.pingpongboss.explodedlayers.ExplodedLayersZOrder.OnTop
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.pingpongboss.explodedlayers.rememberGlassState
import io.github.pingpongboss.explodedlayers.samples.common.animation.InfiniteAnimationEffect
import io.github.pingpongboss.explodedlayers.samples.common.toggle.MultiToggle

private const val MIN_SLIDER_VALUE = 1f / Float.MAX_VALUE

@OptIn(ExperimentalExplodedLayersApi::class)
@Composable
fun AppScreen(content: @Composable () -> Unit) {
    Surface {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(top = 16.dp, bottom = 48.dp)
                    .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val glassState =
                with(LocalDensity.current) {
                    rememberGlassState(
                        tint = Color.DarkGray,
                        tintAlpha = 1f,
                        cornerSize = CornerSize(18.dp),
                    )
                }
            val state =
                rememberExplodedLayersState(
                    interactive = false,
                    initialZOrder = OnTop,
                    initialSpread = MIN_SLIDER_VALUE,
                    glassState = glassState,
                )

            val zOrderOptions = listOf(OnTop, Behind)
            MultiToggle(
                options = zOrderOptions.map { it::class.simpleName ?: "" },
                current = state.zOrder::class.simpleName ?: "",
                onSelectionChange = { selectedString ->
                    state.zOrder = zOrderOptions.first { it::class.simpleName == selectedString }
                },
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var isAnimating by remember { mutableStateOf(true) }
                val progressAnim = remember { Animatable(0f) }
                InfiniteAnimationEffect(isAnimating, progressAnim) { state.spread = it }

                FilledTonalIconButton(onClick = { isAnimating = !isAnimating }) {
                    Icon(
                        imageVector =
                            if (isAnimating) {
                                Icons.Default.Stop
                            } else {
                                Icons.Default.PlayArrow
                            },
                        contentDescription =
                            if (isAnimating) {
                                "Stop animation"
                            } else {
                                "Start animation"
                            },
                    )
                }

                Slider(
                    value = state.spread,
                    onValueChange = { state.spread = it },
                    onValueChangeFinished = { isAnimating = false },
                    valueRange = MIN_SLIDER_VALUE..1f,
                )
            }

            ExplodedLayersRoot(state = state) {
                val shape = RoundedCornerShape(18.dp)
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .clip(shape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = shape,
                            )
                ) {
                    content()
                }
            }
        }
    }
}
