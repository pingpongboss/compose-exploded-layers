package io.github.pingpongboss.explodedlayers.samples.common.platform

import androidx.compose.runtime.Composable

@Composable expect fun isPlatformInDarkTheme(): Boolean

@Composable
expect fun SampleTheme(
    darkTheme: Boolean = isPlatformInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
)
