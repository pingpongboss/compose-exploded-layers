package io.github.pingpongboss.explodedlayers.sample.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object BaseModifier {
    fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
        return if (condition) modifier() else this
    }
}

object ComposableModifier {
    @Composable
    fun Modifier.thenIf(
        condition: Boolean,
        modifier: @Composable Modifier.() -> Modifier,
    ): Modifier {
        return if (condition) modifier() else this
    }
}
