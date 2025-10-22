package io.github.pingpongboss.explodedlayers.samples.common.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

/**
 * A [compositionLocalOf] that provides configuration details about the current layout environment.
 */
val LocalConfiguration = compositionLocalOf {
    Configuration(isLandscape = false, Dp.Unspecified, Dp.Unspecified)
}

/**
 * Represents the configuration of the current layout environment.
 *
 * This data class holds information about the screen's orientation and available dimensions,
 * allowing components to adapt their layout accordingly. It is typically provided down the
 * composition tree via [LocalConfiguration].
 *
 * @property isLandscape True if the current orientation is landscape, false otherwise.
 * @property maxWidth The maximum available width for the layout in Dp.
 * @property maxHeight The maximum available height for the layout in Dp.
 */
data class Configuration(val isLandscape: Boolean, val maxWidth: Dp, val maxHeight: Dp)
