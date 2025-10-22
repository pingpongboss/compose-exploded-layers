package io.github.pingpongboss.explodedlayers.samples.common.platform

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.drawscope.DrawScope

actual fun DrawScope.applyBlur(paint: Paint, blurRadius: Float) {
    // No-op.
}

actual fun DrawScope.applyShader(paint: Paint, shader: Shader, rotation: Float) {
    paint.shader = shader
}
