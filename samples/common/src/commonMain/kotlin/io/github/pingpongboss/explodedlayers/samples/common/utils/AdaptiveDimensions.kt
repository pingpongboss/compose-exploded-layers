package io.github.pingpongboss.explodedlayers.samples.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Dp.adaptive(compact: Float = .8f): Dp {
    val configuration = LocalConfiguration.current
    val scale = configuration.maxHeight / 540.dp * compact

    return if (configuration.maxHeight < 540.dp) this * scale else this
}

@Composable
fun TextStyle.adaptive(compact: Float = .8f): TextStyle {
    val configuration = LocalConfiguration.current
    val scale = configuration.maxHeight / 540.dp * compact

    return if (configuration.maxHeight < 540.dp)
        this.copy(
            fontSize = this.fontSize * scale,
            lineHeight = this.lineHeight * scale,
            letterSpacing = this.letterSpacing * scale,
        )
    else this
}
