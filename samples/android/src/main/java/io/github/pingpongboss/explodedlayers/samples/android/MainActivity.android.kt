package io.github.pingpongboss.explodedlayers.samples.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.samples.android.theme.ExplodedLayersSampleTheme
import io.github.pingpongboss.explodedlayers.samples.common.navigation.TabNavItem
import io.github.pingpongboss.explodedlayers.samples.common.navigation.TabNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ExplodedLayersSampleTheme {
                TabNavigation(
                    tabs =
                        listOf(
                            TabNavItem.Buttons {
                                ExplodedLayersSampleTheme(darkTheme = false) { ButtonsScreen() }
                            },
                            TabNavItem.Simple,
                            TabNavItem.Dialer,
                            TabNavItem.Messages,
                        ),
                    startDestinationLabel = TabNavItem.Buttons.label,
                    header = {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "https://github.com/pingpongboss/compose-exploded-layers",
                            autoSize = TextAutoSize.StepBased(),
                            maxLines = 1,
                        )
                    },
                )
            }
        }
    }
}
