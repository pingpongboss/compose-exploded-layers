package com.github.pingpongboss.explodedlayers.sample.buttons.keycap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pingpongboss.explodedlayers.ExplodedLayersState

@Composable
actual fun KeycapButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    explodedLayersState: ExplodedLayersState
) {
    // not available in wasm
}