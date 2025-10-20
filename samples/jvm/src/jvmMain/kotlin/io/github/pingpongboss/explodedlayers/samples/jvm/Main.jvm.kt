package io.github.pingpongboss.explodedlayers.samples.jvm

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.pingpongboss.explodedlayers.samples.common.navigation.TabNavigation
import io.github.pingpongboss.explodedlayers.samples.common.theme.SampleTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 540.dp, height = 960.dp),
        title = "Exploded Layers (Desktop)",
    ) {
        SampleTheme { TabNavigation(modifier = Modifier.padding(top = 16.dp)) }
    }
}
