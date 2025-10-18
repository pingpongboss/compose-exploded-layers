package io.github.pingpongboss.explodedlayers.samples.android

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExperimentalExplodedLayersApi
import io.github.pingpongboss.explodedlayers.ExplodedLayersDirection.Above
import io.github.pingpongboss.explodedlayers.ExplodedLayersDirection.Behind
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.pingpongboss.explodedlayers.rememberGlassState
import io.github.pingpongboss.explodedlayers.samples.android.toggle.MultiToggle

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
                    initialDirection = Above,
                    initialSpread = MIN_SLIDER_VALUE,
                    glassState = glassState,
                )

            val directionOptions = listOf(Above, Behind)
            MultiToggle(
                options = directionOptions.map { it::class.simpleName ?: "" },
                current = state.direction::class.simpleName ?: "",
                onSelectionChange = { selectedString ->
                    state.direction =
                        directionOptions.first { it::class.simpleName == selectedString }
                },
            )

            Slider(
                value = state.spread,
                onValueChange = { state.spread = it },
                valueRange = MIN_SLIDER_VALUE..1f,
            )

            ExplodedLayersRoot(state = state) {
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(18.dp),
                            )
                ) {
                    content()
                }
            }
        }
    }
}
