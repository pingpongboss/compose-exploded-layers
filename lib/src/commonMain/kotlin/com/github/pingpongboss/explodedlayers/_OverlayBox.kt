package com.github.pingpongboss.explodedlayers

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

/**
 * A layout composable that overlays two pieces of content where the second ("overlay") is measured
 * to exactly match the size of the first ("base").
 *
 * This is similar to [Box], but it ensures the overlay content always adopts the exact measured
 * dimensions of the base content, even if the overlay's own intrinsic size would otherwise differ.
 *
 * This makes it ideal for drawing highlights, masks, or visual effects that should always cover the
 * same region as another composable.
 *
 * Example usage:
 * ```
 * OverlayBox {
 *     base {
 *         Image(painterResource(R.drawable.photo), contentDescription = null)
 *     }
 *     overlay {
 *         Box(Modifier.background(Color.Black.copy(alpha = 0.3f)))
 *     }
 * }
 * ```
 *
 * In this example, the translucent black overlay is automatically sized to exactly match the image,
 * with no frame delay or recomposition as would be the case with
 * [androidx.compose.ui.layout.onGloballyPositioned].
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param content A receiver lambda where the base and overlay composables are defined using
 *   [OverlayBoxScope.base] and [OverlayBoxScope.overlay].
 */
@Composable
internal fun OverlayBox(modifier: Modifier = Modifier, content: OverlayBoxScope.() -> Unit) {
    val scope = OverlayBoxScopeImpl().apply(content)

    val baseContent = scope.base
    val overlayContent = scope.overlay

    require(baseContent != null) { "EqualSizeBox requires a base() content" }
    require(overlayContent != null) { "EqualSizeBox requires an overlay() content" }

    Layout(
        modifier = modifier,
        content = {
            Box { baseContent() }
            Box { overlayContent() }
        },
    ) { measurables, constraints ->
        val base = measurables[0].measure(constraints)
        val overlay = measurables[1].measure(Constraints.fixed(base.width, base.height))
        layout(base.width, base.height) {
            base.placeRelative(0, 0)
            overlay.placeRelative(0, 0)
        }
    }
}

/**
 * Scope for the [OverlayBox] content lambda.
 *
 * Provides DSL-style functions for defining the base and overlay composables.
 *
 * The [base] composable determines the size of the [OverlayBox], and the [overlay] composable is
 * measured using that same size. Both are positioned at the same origin, so the overlay visually
 * sits on top of the base.
 */
interface OverlayBoxScope {

    /**
     * Defines the base composable whose measured size determines the size of the entire
     * [OverlayBox].
     *
     * This must be called exactly once within the [OverlayBox] content block.
     */
    fun base(content: @Composable () -> Unit)

    /**
     * Defines the overlay composable that will be measured and laid out to match the exact size of
     * the base composable.
     *
     * This must be called exactly once within the [OverlayBox] content block.
     */
    fun overlay(content: @Composable () -> Unit)
}

private class OverlayBoxScopeImpl : OverlayBoxScope {

    var base: (@Composable () -> Unit)? = null
    var overlay: (@Composable () -> Unit)? = null

    override fun base(content: @Composable () -> Unit) {
        base = content
    }

    override fun overlay(content: @Composable () -> Unit) {
        overlay = content
    }
}
