package com.github.pingpongboss.explodedlayers.sample.buttons.hovereffects

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pingpongboss.explodedlayers.ExplodedLayersState

@Composable
expect fun GradientButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    explodedLayersState: ExplodedLayersState,
)