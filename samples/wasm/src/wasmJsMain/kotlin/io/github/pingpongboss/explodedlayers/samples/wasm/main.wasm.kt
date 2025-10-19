package io.github.pingpongboss.explodedlayers.samples.wasm

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import io.github.pingpongboss.explodedlayers.samples.common.navigation.TabNavigation
import io.github.pingpongboss.explodedlayers.samples.common.theme.SampleTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        SampleTheme { TabNavigation(modifier = Modifier.padding(top = 16.dp)) }
    }
}
