package com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pingpongboss.explodedlayers.ExplodedLayersState

@Composable
actual fun GradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    explodedLayersState: ExplodedLayersState
) {
    // not available in wasm
}