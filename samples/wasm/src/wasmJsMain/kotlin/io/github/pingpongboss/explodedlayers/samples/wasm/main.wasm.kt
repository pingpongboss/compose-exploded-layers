package io.github.pingpongboss.explodedlayers.samples.wasm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import io.github.pingpongboss.explodedlayers.samples.common.SimpleScreen
import io.github.pingpongboss.explodedlayers.samples.wasm.ui.theme.ExplodedlayersSampleTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        ExplodedlayersSampleTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SimpleScreen(innerPadding)
            }
        }
    }
}
