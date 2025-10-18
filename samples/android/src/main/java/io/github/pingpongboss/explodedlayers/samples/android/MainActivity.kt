package io.github.pingpongboss.explodedlayers.samples.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.pingpongboss.explodedlayers.samples.android.theme.ExplodedLayersSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         enableEdgeToEdge()
 
         setContent { ExplodedLayersSampleTheme { MainScreen() } }
     }
 }
 
 sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
 
     data object Buttons : BottomNavItem("buttons", Icons.Default.SmartButton, "Buttons")
 
     data object Dialer : BottomNavItem("dialer", Icons.Default.Phone, "Dialer")
 }
 
 @Composable
 private fun MainScreen() {
     val navController = rememberNavController()
    val navItems = listOf(BottomNavItem.Buttons, BottomNavItem.Dialer)

     Scaffold(
         bottomBar = {
             NavigationBar {
                 val navBackStackEntry by navController.currentBackStackEntryAsState()
                 val currentDestination = navBackStackEntry?.destination
 
                 navItems.forEach { screen ->
                     NavigationBarItem(
                         selected =
                             currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                         onClick = {
                             navController.navigate(screen.route) {
                                 popUpTo(navController.graph.findStartDestination().id) {
                                     saveState = true
                                 }
                                 launchSingleTop = true
                                 restoreState = true
                             }
                         },
                         icon = { Icon(screen.icon, contentDescription = screen.label) },
                         label = { Text(screen.label) },
                     )
                 }
             }
         }
     ) { innerPadding ->
         Column(
             modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
             verticalArrangement = Arrangement.spacedBy(16.dp),
             horizontalAlignment = Alignment.CenterHorizontally,
         ) {
             Text(
                 modifier = Modifier.padding(horizontal = 16.dp),
                 text = "https://github.com/pingpongboss/compose-exploded-layers",
                 autoSize = TextAutoSize.StepBased(),
                 maxLines = 1,
             )
 
             NavHost(
                 navController = navController,
                 startDestination = BottomNavItem.Buttons.route,
                 modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
             ) {
                 composable(BottomNavItem.Buttons.route) {
                     ExplodedLayersSampleTheme(darkTheme = false) { ButtonsScreen() }
                }
                composable(BottomNavItem.Dialer.route) {
                    ExplodedLayersSampleTheme(darkTheme = true) { DialerScreen() }
                }
            }
        }
    }
}
