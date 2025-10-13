package io.github.pingpongboss.explodedlayers

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import kotlin.math.max

/**
 * Adds padding around the content up to a *safe* amount that will not cause the child to shrink
 * unnaturally or violate the parent’s layout constraints.
 *
 * This modifier behaves like [androidx.compose.foundation.layout.padding], but with additional
 * safeguards:
 * 1. It never applies padding that would exceed the parent’s maximum constraints.
 * 2. It never applies padding that would cause the child to be measured smaller than its natural,
 *    unconstrained size.
 *
 * In practice, this is useful for layouts where the parent constraints may vary (for example, when
 * animating layout size or applying offsets), ensuring that padding behaves gracefully even under
 * tight constraints.
 *
 * ### Example
 *
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(100.dp)
 *         .safePadding(start = 24.dp, end = 24.dp)
 * ) {
 *     Text("Clamped padding keeps this readable")
 * }
 * ```
 *
 * In this example, if the total requested padding (48.dp) would exceed the available horizontal
 * space, it will automatically be reduced so that the child still fits naturally within the box.
 *
 * @param start Padding applied to the start edge, clamped to a safe amount.
 * @param top Padding applied to the top edge, clamped to a safe amount.
 * @param end Padding applied to the end edge, clamped to a safe amount.
 * @param bottom Padding applied to the bottom edge, clamped to a safe amount.
 */
internal fun Modifier.safePadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): Modifier = this.then(SafePaddingModifier(start, top, end, bottom))

private data class SafePaddingModifier(val start: Dp, val top: Dp, val end: Dp, val bottom: Dp) :
    LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val startPx = start.roundToPx()
        val topPx = top.roundToPx()
        val endPx = end.roundToPx()
        val bottomPx = bottom.roundToPx()

        // First, measure child with *no padding applied* to see its natural size
        val naturalPlaceable = measurable.measure(constraints)

        // Available space after child’s natural size
        val availableWidth = max(0, constraints.maxWidth - naturalPlaceable.width)
        val availableHeight = max(0, constraints.maxHeight - naturalPlaceable.height)

        // Clamp padding so it doesn’t reduce child below natural size
        val appliedStart = minOf(startPx, availableWidth / 2)
        val appliedEnd = minOf(endPx, availableWidth - appliedStart, availableWidth / 2)
        val appliedTop = minOf(topPx, availableHeight / 2)
        val appliedBottom = minOf(bottomPx, availableHeight - appliedTop, availableHeight / 2)

        // Now re-measure child with reduced constraints (if necessary)
        val childConstraints =
            constraints.offset(-appliedStart - appliedEnd, -appliedTop - appliedBottom)
        val finalPlaceable = measurable.measure(childConstraints)

        val width = appliedStart + finalPlaceable.width + appliedEnd
        val height = appliedTop + finalPlaceable.height + appliedBottom

        return layout(width, height) { finalPlaceable.placeRelative(appliedStart, appliedTop) }
    }
}
