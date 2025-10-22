package io.github.pingpongboss.explodedlayers.samples.common.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.samples.common.AppScreen
import io.github.pingpongboss.explodedlayers.samples.common.ButtonsScreen
import io.github.pingpongboss.explodedlayers.samples.common.DialerApp
import io.github.pingpongboss.explodedlayers.samples.common.MessagesApp
import io.github.pingpongboss.explodedlayers.samples.common.SimpleScreen
import io.github.pingpongboss.explodedlayers.samples.common.dimension.Configuration
import io.github.pingpongboss.explodedlayers.samples.common.dimension.LocalConfiguration
import io.github.pingpongboss.explodedlayers.samples.common.theme.SampleTheme

/**
 * Represents a single navigation item in a tab-based layout.
 *
 * This sealed class defines the structure for each tab, including its visual representation and the
 * content to be displayed when the tab is selected. It's used to populate navigation components
 * like [NavigationBar] and [NavigationRail].
 *
 * @property icon The [ImageVector] to be displayed for the tab.
 * @property label The text label for the tab, used for display and as a unique identifier.
 * @property content The composable function that renders the screen content for this tab. It
 *   receives [PaddingValues] to apply appropriate padding from the scaffold.
 */
sealed class TabNavItem(
    val icon: ImageVector,
    val label: String,
    val content: @Composable (innerPadding: PaddingValues) -> Unit,
) {

    data object Buttons :
        TabNavItem(
            icon = Icons.Default.SmartButton,
            label = "Buttons",
            content = { SampleTheme(darkTheme = false) { ButtonsScreen() } },
        )

    data object Simple :
        TabNavItem(
            icon = Icons.Default.Favorite,
            label = "Simple",
            content = { SimpleScreen(innerPadding = it) },
        )

    data object Dialer :
        TabNavItem(
            icon = Icons.Default.Phone,
            label = "Dialer",
            content = {
                SampleTheme(darkTheme = true) { AppScreen(innerPadding = it) { DialerApp() } }
            },
        )

    data object Messages :
        TabNavItem(
            icon = Icons.AutoMirrored.Filled.Message,
            label = "Messages",
            content = {
                SampleTheme(darkTheme = true) { AppScreen(innerPadding = it) { MessagesApp() } }
            },
        )
}

/**
 * A top-level navigation composable that adapts its layout based on the screen orientation. It uses
 * a [BottomNavigationScaffold] for portrait mode and a [SideNavigationScaffold] for landscape mode.
 * This component manages the state of the selected tab and displays the corresponding content.
 *
 * It uses [BoxWithConstraints] to determine the orientation and provides a [LocalConfiguration] to
 * its children, which contains information about the orientation and screen dimensions.
 *
 * @param modifier The [Modifier] to be applied to the container.
 * @param tabs The list of [TabNavItem]s to be displayed in the navigation. Defaults to a predefined
 *   list of Simple, Dialer, and Messages tabs.
 * @param startDestinationLabel The label of the tab that should be selected initially. Defaults to
 *   the label of the [TabNavItem.Simple] tab.
 * @param header A composable lambda that renders the header content, displayed only in portrait
 *   mode above the main content area. Defaults to a [Text] displaying a URL.
 */
@Composable
fun TabNavigation(
    modifier: Modifier = Modifier,
    tabs: List<TabNavItem> =
        listOf(TabNavItem.Buttons, TabNavItem.Simple, TabNavItem.Dialer, TabNavItem.Messages),
    startDestinationLabel: String = TabNavItem.Buttons.label,
    header: @Composable () -> Unit = {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "https://github.com/pingpongboss/compose-exploded-layers",
            maxLines = 1,
        )
    },
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        var selectedTabLabel by rememberSaveable { mutableStateOf(startDestinationLabel) }

        val isLandscape = maxWidth > maxHeight
        CompositionLocalProvider(
            LocalConfiguration provides Configuration(isLandscape, maxWidth, maxHeight)
        ) {
            if (isLandscape) {
                SideNavigationScaffold(
                    modifier = modifier,
                    tabs = tabs,
                    selectedTabLabel = selectedTabLabel,
                    onSelectedTabLabelChange = { selectedTabLabel = it },
                )
            } else {
                BottomNavigationScaffold(
                    tabs = tabs,
                    selectedTabLabel = selectedTabLabel,
                    onSelectedTabLabelChange = { selectedTabLabel = it },
                    header = header,
                )
            }
        }
    }
}

@Composable
private fun SideNavigationScaffold(
    modifier: Modifier,
    tabs: List<TabNavItem>,
    selectedTabLabel: String,
    onSelectedTabLabelChange: (String) -> Unit,
) {
    Scaffold { innerPadding ->
        Row(modifier.fillMaxHeight()) {
            NavigationRail {
                tabs.forEach { item ->
                    NavigationRailItem(
                        selected = selectedTabLabel == item.label,
                        onClick = { onSelectedTabLabelChange(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Crossfade(targetState = selectedTabLabel) { label ->
                    tabs.first { it.label == label }.content(innerPadding)
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationScaffold(
    tabs: List<TabNavItem>,
    selectedTabLabel: String,
    onSelectedTabLabelChange: (String) -> Unit,
    header: @Composable (() -> Unit),
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { item ->
                    NavigationBarItem(
                        selected = selectedTabLabel == item.label,
                        onClick = { onSelectedTabLabelChange(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header()

            Box(Modifier.fillMaxSize()) {
                Crossfade(targetState = selectedTabLabel) { label ->
                    tabs.first { it.label == label }.content(innerPadding)
                }
            }
        }
    }
}
