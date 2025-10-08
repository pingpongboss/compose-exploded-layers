package com.github.pingpongboss.explodedlayers.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.github.pingpongboss.explodedlayers.sample.theme.ExplodedLayersSampleTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        ExplodedLayersSampleTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SampleRoot(innerPadding)
            }
        }
    }
}