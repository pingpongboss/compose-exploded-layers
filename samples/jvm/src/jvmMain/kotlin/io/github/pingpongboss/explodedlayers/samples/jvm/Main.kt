package io.github.pingpongboss.explodedlayers.samples.jvm

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.pingpongboss.explodedlayers.samples.common.SampleBody

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Exploded Layers (Desktop)") {
        SampleBody(PaddingValues.Zero)
    }
}
