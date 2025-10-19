package io.github.pingpongboss.explodedlayers.samples.common.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.samples.common.AppScreen
import io.github.pingpongboss.explodedlayers.samples.common.DialerApp
import io.github.pingpongboss.explodedlayers.samples.common.MessagesApp
import io.github.pingpongboss.explodedlayers.samples.common.SimpleScreen

sealed class TabNavItem(
    val icon: ImageVector,
    val label: String,
    val content: @Composable () -> Unit,
) {

    class Buttons(content: @Composable () -> Unit) :
        TabNavItem(icon = Icons.Default.SmartButton, label = label, content = content) {
        companion object {
            @Suppress("ConstPropertyName") const val label = "Buttons"
        }
    }

    data object Simple :
        TabNavItem(
            icon = Icons.Default.Favorite,
            label = "Simple",
            content = { SimpleScreen(PaddingValues.Zero) },
        )

    data object Dialer :
        TabNavItem(
            icon = Icons.Default.Phone,
            label = "Dialer",
            content = { AppScreen { DialerApp() } },
        )

    data object Messages :
        TabNavItem(
            icon = Icons.AutoMirrored.Filled.Message,
            label = "Messages",
            content = { AppScreen { MessagesApp() } },
        )
}

@Composable
fun TabNavigation(
    modifier: Modifier = Modifier,
    tabs: List<TabNavItem> = listOf(TabNavItem.Simple, TabNavItem.Dialer, TabNavItem.Messages),
    startDestinationLabel: String = TabNavItem.Simple.label,
    header: @Composable () -> Unit = {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "https://github.com/pingpongboss/compose-exploded-layers",
            maxLines = 1,
        )
    },
) {
    var selectedTabLabel by rememberSaveable { mutableStateOf(startDestinationLabel) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTabLabel == item.label,
                        onClick = { selectedTabLabel = item.label },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header()

            Box(Modifier.fillMaxSize()) {
                Crossfade(targetState = selectedTabLabel) { label ->
                    tabs.first { it.label == label }.content()
                }
            }
        }
    }
}
