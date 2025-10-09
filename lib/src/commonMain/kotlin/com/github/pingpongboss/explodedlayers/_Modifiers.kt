package com.github.pingpongboss.explodedlayers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun Modifier.thenIf(
    condition: Boolean,
    modifier: @Composable Modifier.() -> Modifier,
): Modifier {
    return if (condition) modifier() else this
}

@Composable
internal fun <T> Modifier.thenIfNotNull(
    value: T?,
    modifier: @Composable Modifier.(T) -> Modifier,
): Modifier {
    return if (value != null) modifier(value) else this
}
