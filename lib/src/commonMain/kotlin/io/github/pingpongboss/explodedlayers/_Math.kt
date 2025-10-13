package io.github.pingpongboss.explodedlayers

import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sign

/**
 * Maps the magnitude of `x` to a smooth, saturating range `(0, 1)`, preserving the sign of `x`.
 *
 * The curve follows a one-parameter logistic form controlled by [halfLife], where:
 * - `|x| = |halfLife|` ≈ 0.5
 * - `|x| = 0` ≈ 0
 * - `|x| → ±∞` ≈ ±1
 *
 * This function behaves like a continuous, symmetric normalization curve — useful for smoothly
 * compressing large values toward ±1 while keeping small values near 0.
 *
 * @param x Input value to normalize.
 * @param halfLife Controls steepness; smaller absolute values make the curve reach ±1 more quickly.
 * @return A signed float in the range (-1, 1), approaching ±1 as |x| increases.
 */
internal fun normalize(x: Float, halfLife: Float = 1f): Float {
    val h = abs(halfLife)

    val p0 = 1e-3f // f(0) ≈ 0.001 (near zero)
    val k = ln((1f - p0) / p0) / h // binds k to the given half-life

    val sign = sign(x)
    return sign * 1f / (1f + exp(-k * (abs(x) - h)))
}

internal fun abs(dp: Dp): Dp {
    return Dp(abs(dp.value))
}
