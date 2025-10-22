package io.github.pingpongboss.explodedlayers.samples.common.utils

import kotlin.math.PI

object Math {
    /**
     * Converts an angle measured in degrees to an approximately equivalent angle measured in
     * radians.
     */
    fun toRadians(deg: Double): Double = deg / 180.0 * PI
}
