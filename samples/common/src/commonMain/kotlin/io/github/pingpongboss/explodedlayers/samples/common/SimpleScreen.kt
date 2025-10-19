package io.github.pingpongboss.explodedlayers.samples.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.ExplodedLayersRoot
import io.github.pingpongboss.explodedlayers.ExplodedLayersState
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.rememberExplodedLayersState
import io.github.pingpongboss.explodedlayers.separateLayer

private const val MIN_SLIDER_VALUE = 1f / Float.MAX_VALUE

@Composable
fun SimpleScreen(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val state = rememberExplodedLayersState(initialSpread = 0f)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Explode layers:")
                Slider(
                    value = state.spread,
                    onValueChange = { state.spread = it },
                    onValueChangeFinished = {
                        if (state.spread == MIN_SLIDER_VALUE) {
                            state.spread = 0f
                        }
                    },
                    valueRange = MIN_SLIDER_VALUE..1f,
                )
            }

            MyCustomButton(label = "Hello world", state = state)
        }
    }
}

@Composable
private fun MyCustomButton(
    label: String,
    modifier: Modifier = Modifier,
    state: ExplodedLayersState = rememberExplodedLayersState(),
) {
    ExplodedLayersRoot(state = state, modifier = modifier) {
        Box(
            Modifier.background(MaterialTheme.colorScheme.primary)
                .padding(12.dp)
                .separateLayer()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(12.dp)
        ) {
            SeparateLayer {
                Text(text = label, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}
