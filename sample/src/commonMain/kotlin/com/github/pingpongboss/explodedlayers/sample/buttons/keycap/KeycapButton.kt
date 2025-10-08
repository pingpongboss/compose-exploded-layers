package com.github.pingpongboss.explodedlayers.sample.buttons.keycap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pingpongboss.explodedlayers.ExplodedLayersState
import com.github.pingpongboss.explodedlayers.rememberExplodedLayersState

@Composable
expect fun KeycapButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    explodedLayersState: ExplodedLayersState = rememberExplodedLayersState(),
)