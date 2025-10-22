package io.github.pingpongboss.explodedlayers.samples.common.utils

import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.drawscope.DrawScope

actual fun DrawScope.applyBlur(paint: Paint, blurRadius: Float) {
    paint.asFrameworkPaint().apply {
        this.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }
}

actual fun DrawScope.applyShader(paint: Paint, shader: Shader, rotation: Float) {
    paint.asFrameworkPaint().apply {
        val matrix =
            android.graphics.Matrix().apply {
                // Rotate first
                setRotate(rotation, center.x, center.y)
                // Stretch horizontally (or vertically) to match button aspect ratio
                val scaleX = size.width / size.height
                val scaleY = 1f
                this.postScale(scaleX, scaleY, center.x, center.y)
            }
        shader.setLocalMatrix(matrix)

        this.shader = shader
    }
}
