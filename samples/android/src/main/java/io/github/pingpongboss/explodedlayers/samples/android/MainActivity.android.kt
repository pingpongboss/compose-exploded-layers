package io.github.pingpongboss.explodedlayers.samples.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.pingpongboss.explodedlayers.samples.common.navigation.TabNavigation
import io.github.pingpongboss.explodedlayers.samples.common.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent { SampleTheme { TabNavigation() } }
    }
}
