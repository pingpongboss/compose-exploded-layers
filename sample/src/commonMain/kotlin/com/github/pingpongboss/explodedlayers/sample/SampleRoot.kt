package com.github.pingpongboss.explodedlayers.sample

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.pingpongboss.explodedlayers.ExplodedLayersState
import com.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.CreepButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ExciteButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ExpandButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.GradientButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.LayersButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ShadyButton
import com.github.pingpongboss.explodedlayers.sample.buttons.keycap.KeycapButton
import com.github.pingpongboss.explodedlayers.sample.grid.Grid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun SampleRoot(innerPadding: PaddingValues) {
    Column(
        modifier =
            Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "https://github.com/pingpongboss/compose-exploded-layers",
            autoSize = TextAutoSize.StepBased(),
            maxLines = 1,
        )

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
                    imageVector = if (!isAnimating) Icons.Default.PlayArrow else Icons.Default.Stop,
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
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                explodedLayersState1 = explodedLayersState1,
                explodedLayersState2 = explodedLayersState2,
            )
        }
    }
}

@Composable
private fun InfiniteAnimationEffect(
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
                        if (explodedLayersState1.spread == MIN_SLIDER_VALUE) {
                            explodedLayersState1.spread = 0f
                        }
                    },
                    valueRange = MIN_SLIDER_VALUE..1f,
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
                        if (explodedLayersState2.spread == MIN_SLIDER_VALUE) {
                            explodedLayersState2.spread = 0f
                        }
                    },
                    valueRange = MIN_SLIDER_VALUE..1f,
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

private const val MIN_SLIDER_VALUE = 1f / Float.MAX_VALUE
private val EXPLODED_LAYERS_STATE_1_INITIAL_OFFSET = DpOffset(x = -40.dp, y = 40.dp)
private val EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET = DpOffset(x = -20.dp, y = 20.dp)
