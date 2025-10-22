package io.github.pingpongboss.explodedlayers.samples.wasm

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import io.github.pingpongboss.explodedlayers.samples.common.TabNavigation
import io.github.pingpongboss.explodedlayers.samples.common.platform.SampleTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val frame = document.getElementById("phone-frame")!!
    ComposeViewport(frame) {
        SampleTheme { TabNavigation(modifier = Modifier.padding(top = 16.dp)) }
    }
}
