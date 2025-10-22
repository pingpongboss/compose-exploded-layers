package io.github.pingpongboss.explodedlayers.samples.common.platform

import androidx.compose.runtime.Composable
import io.github.pingpongboss.explodedlayers.samples.common.theme.SampleThemeBase

@Composable actual fun isPlatformInDarkTheme(): Boolean = false

@Composable
actual fun SampleTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable (() -> Unit),
) = SampleThemeBase(darkTheme, dynamicColor, content)
