package io.github.pingpongboss.explodedlayers.samples.common

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.CreepButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.ExciteButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.ExpandButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.GradientButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.LayersButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.hovereffects.ShadyButton
import io.github.pingpongboss.explodedlayers.samples.common.buttons.keycap.KeycapButton
import io.github.pingpongboss.explodedlayers.samples.common.layout.Grid
import io.github.pingpongboss.explodedlayers.samples.common.utils.InfiniteAnimationEffect
import kotlinx.coroutines.launch

private val EXPLODED_LAYERS_STATE_1_INITIAL_OFFSET = DpOffset(x = -40.dp, y = 40.dp)
private val EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET = DpOffset(x = -20.dp, y = 20.dp)

@Composable
fun ButtonsScreen() {
    Surface {
        Column(
            modifier = Modifier.padding(top = 16.dp).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val explodedLayersState1 =
                rememberExplodedLayersState(
                    offset = EXPLODED_LAYERS_STATE_1_INITIAL_OFFSET,
                    interactive = true,
                    initialSpread = 0f,
                )

            val explodedLayersState2 =
                rememberExplodedLayersState(
                    offset = EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET,
                    interactive = false,
                    initialSpread = 0f,
                )

            var isAnimating by remember { mutableStateOf(false) }
            val progressAnim = remember { Animatable(0f) }
            InfiniteAnimationEffect(isAnimating, progressAnim) { explodedLayersState1.spread = it }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        isAnimating = !isAnimating
                        scope.launch {
                            progressAnim.snapTo(0f)
                            explodedLayersState1.spread = 0f
                        }
                    }
                ) {
                    Icon(
                        imageVector =
                            if (!isAnimating) Icons.Default.PlayArrow else Icons.Default.Stop,
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = if (!isAnimating) "Start animation" else "Stop animation")
                }

                Button(
                    onClick = {
                        isAnimating = false
                        scope.launch {
                            progressAnim.snapTo(0f)

                            explodedLayersState1.offset = EXPLODED_LAYERS_STATE_1_INITIAL_OFFSET
                            explodedLayersState1.spread = 0f

                            explodedLayersState2.offset = EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET
                            explodedLayersState2.spread = 0f
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Restore, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(text = "Reset all")
                }
            }

            Box(
                modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                SampleGrid(
                    explodedLayersState1 = explodedLayersState1,
                    explodedLayersState2 = explodedLayersState2,
                )
            }
        }
    }
}

@Composable
private fun SampleGrid(
    modifier: Modifier = Modifier,
    explodedLayersState1: ExplodedLayersState,
    explodedLayersState2: ExplodedLayersState,
) {
    val windowInfo = LocalWindowInfo.current
    val columns =
        with(LocalDensity.current) {
            when {
                windowInfo.containerSize.width.toDp() < 600.dp -> 2
                else -> 4
            }
        }

    Grid(
        columns = columns,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        line { Spacer(Modifier.height(16.dp)) }

        line {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Explode layers:")

                Slider(
                    value = explodedLayersState1.spread,
                    onValueChange = { explodedLayersState1.spread = it },
                    onValueChangeFinished = {
                        if (explodedLayersState1.spread == Float.MIN_VALUE) {
                            explodedLayersState1.spread = 0f
                        }
                    },
                    valueRange = Float.MIN_VALUE..1f,
                )
            }
        }

        line {
            KeycapButton(
                label = "+ Add to cart",
                onClick = {},
                modifier = Modifier.padding(vertical = 16.dp * explodedLayersState1.spread),
                explodedLayersState = explodedLayersState1,
            )
        }

        line { Spacer(Modifier.height(16.dp)) }

        line {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Explode layers:")

                Slider(
                    value = explodedLayersState2.spread,
                    onValueChange = { explodedLayersState2.spread = it },
                    onValueChangeFinished = {
                        if (explodedLayersState2.spread == Float.MIN_VALUE) {
                            explodedLayersState2.spread = 0f
                        }
                    },
                    valueRange = Float.MIN_VALUE..1f,
                )
            }
        }

        item {
            LayersButton(
                label = "Layers",
                colors = listOf(Color(0xff52b29c), Color(0xFFF5BC45), Color(0xFF6079F6)),
                onClick = {},
                explodedLayersState = explodedLayersState2,
            )
        }

        item {
            ShadyButton(label = "Shady", onClick = {}, explodedLayersState = explodedLayersState2)
        }

        item {
            GradientButton(
                label = "Gradient",
                onClick = {},
                explodedLayersState = explodedLayersState2,
            )
        }

        item {
            ExpandButton(label = "Expand", onClick = {}, explodedLayersState = explodedLayersState2)
        }

        item {
            CreepButton(label = "Creep", onClick = {}, explodedLayersState = explodedLayersState2)
        }

        item {
            ExciteButton(label = "Excite", onClick = {}, explodedLayersState = explodedLayersState2)
        }
    }
}
