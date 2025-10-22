package io.github.pingpongboss.explodedlayers.samples.common.platform

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.pingpongboss.explodedlayers.samples.common.theme.DarkColorScheme
import io.github.pingpongboss.explodedlayers.samples.common.theme.LightColorScheme
import io.github.pingpongboss.explodedlayers.samples.common.theme.Typography

@Composable actual fun isPlatformInDarkTheme(): Boolean = isSystemInDarkTheme()

@Composable
actual fun SampleTheme(darkTheme: Boolean, dynamicColor: Boolean, content: @Composable () -> Unit) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
