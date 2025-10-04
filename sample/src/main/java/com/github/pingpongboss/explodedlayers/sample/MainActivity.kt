package com.github.pingpongboss.explodedlayers.sample

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.CreepButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ExciteButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ExpandButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.GradientButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.LayersButton
import com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects.ShadyButton
import com.github.pingpongboss.explodedlayers.sample.buttons.keycap.KeycapButton
import com.github.pingpongboss.explodedlayers.sample.grid.Grid
import com.github.pingpongboss.explodedlayers.sample.theme.ExplodedLayersSampleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private const val MIN_SLIDER_VALUE = 1f / Float.MAX_VALUE
private val EXPLODED_LAYERS_STATE_1_INITIAL_OFFSET = DpOffset(x = -40.dp, y = 40.dp)
private val EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET = DpOffset(x = -20.dp, y = 20.dp)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT))

        setContent {
            ExplodedLayersSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleGrid(innerPadding)
                }
            }
        }
    }
}

@Composable
private fun SampleGrid(innerPadding: PaddingValues) {
    Column(
        modifier =
            Modifier.verticalScroll(rememberScrollState())
                .padding(innerPadding)
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
            )
        var explodeLayersProgress1 by rememberSaveable { mutableFloatStateOf(0f) }
        explodedLayersState1.spread = explodeLayersProgress1

        val explodedLayersState2 =
            rememberExplodedLayersState(
                offset = EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET,
                interactive = false,
            )
        var explodeLayersProgress2 by rememberSaveable { mutableFloatStateOf(0f) }
        explodedLayersState2.spread = explodeLayersProgress2

        var isAnimating by remember { mutableStateOf(false) }
        val progressAnim = remember { Animatable(0f) }
        LaunchedEffect(isAnimating) {
            if (isAnimating) {
                launch {
                    while (true) {
                        progressAnim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 2000),
                        )
                        delay(1.seconds)
                        progressAnim.animateTo(
                            targetValue = MIN_SLIDER_VALUE,
                            animationSpec = tween(durationMillis = 2000),
                        )
                        delay(1.seconds)
                    }
                }
            }
        }
        if (isAnimating) {
            explodeLayersProgress1 = progressAnim.value
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val scope = rememberCoroutineScope()

            Button(
                onClick = {
                    isAnimating = !isAnimating
                    scope.launch {
                        progressAnim.snapTo(0f)
                        explodeLayersProgress1 = 0f
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
                        explodeLayersProgress1 = 0f

                        explodedLayersState2.offset = EXPLODED_LAYERS_STATE_2_INITIAL_OFFSET
                        explodeLayersProgress2 = 0f
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Restore, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(text = "Reset all")
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                            value = explodeLayersProgress1,
                            onValueChange = { explodeLayersProgress1 = it },
                            onValueChangeFinished = {
                                if (explodeLayersProgress1 == MIN_SLIDER_VALUE) {
                                    explodeLayersProgress1 = 0f
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
                        modifier =
                            Modifier.padding(
                                vertical = if (isAnimating) 16.dp * explodeLayersProgress1 else 0.dp
                            ),
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
                            value = explodeLayersProgress2,
                            onValueChange = { explodeLayersProgress2 = it },
                            onValueChangeFinished = {
                                if (explodeLayersProgress2 == MIN_SLIDER_VALUE) {
                                    explodeLayersProgress2 = 0f
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
                    ShadyButton(
                        label = "Shady",
                        onClick = {},
                        explodedLayersState = explodedLayersState2,
                    )
                }

                item {
                    GradientButton(
                        label = "Gradient",
                        onClick = {},
                        explodedLayersState = explodedLayersState2,
                    )
                }

                item {
                    ExpandButton(
                        label = "Expand",
                        onClick = {},
                        explodedLayersState = explodedLayersState2,
                    )
                }

                item {
                    CreepButton(
                        label = "Creep",
                        onClick = {},
                        explodedLayersState = explodedLayersState2,
                    )
                }

                item {
                    ExciteButton(
                        label = "Excite",
                        onClick = {},
                        explodedLayersState = explodedLayersState2,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SampleGridPreview() {
    ExplodedLayersSampleTheme { SampleGrid(PaddingValues.Zero) }
}
