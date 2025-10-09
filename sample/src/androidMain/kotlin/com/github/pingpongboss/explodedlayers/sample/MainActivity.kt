package com.github.pingpongboss.explodedlayers.sample

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.github.pingpongboss.explodedlayers.sample.theme.ExplodedLayersSampleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT))

        setContent {
            ExplodedLayersSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleRoot(innerPadding)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SampleRootPreview() {
    ExplodedLayersSampleTheme { SampleRoot(PaddingValues.Zero) }
}
