package io.github.pingpongboss.explodedlayers.samples.common.theme

import androidx.compose.runtime.Composable

@Composable actual fun isPlatformInDarkTheme(): Boolean = false

@Composable
actual fun SampleTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable (() -> Unit),
) = SampleThemeBase(darkTheme, dynamicColor, content)
