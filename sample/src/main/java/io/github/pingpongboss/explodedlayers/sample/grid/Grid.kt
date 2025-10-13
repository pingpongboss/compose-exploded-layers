package io.github.pingpongboss.explodedlayers.sample.grid

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * A flexible grid layout composable that arranges its children into a specified grid.
 *
 * The grid is constructed using a [GridScope], which allows you to place items spanning one or more
 * columns, or insert full-width lines. Items are automatically chunked into rows according to their
 * declared span size, and incomplete rows are padded with empty spans so that all rows align
 * correctly.
 *
 * @param columns The total number of columns in the grid. Must be ≥ 1.
 * @param modifier Modifier applied to the root [Column] of the grid.
 * @param verticalArrangement The vertical arrangement of rows inside the grid’s root [Column].
 * @param horizontalArrangement The horizontal arrangement of items within each row.
 * @param verticalAlignment The vertical alignment of items inside each row.
 * @param horizontalAlignment The horizontal alignment of rows inside the grid’s root [Column].
 * @param content The grid’s children, defined inside a [GridScope]. Use [GridScope.item] to place
 *   an item with a specified column span, or [GridScope.line] to insert a full-width item.
 *
 * ### Example
 *
 * ```
 * Grid(columns = 3) {
 *     item { Text("A") }
 *     item(columns = 2) { Text("B spans 2 columns") }
 *     line { Divider() }
 *     item { Text("C") }
 *     item { Text("D") }
 *     item { Text("E") }
 * }
 * ```
 *
 * This example produces a grid with 3 columns per row, inserting items that span across different
 * widths and ensuring that rows align correctly.
 *
 * @throws IllegalArgumentException if [columns] is less than 1, or if a [GridScope.item] is
 *   declared with a span greater than [columns].
 */
@Composable
fun Grid(
    @IntRange(from = 1) columns: Int,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: GridScope.() -> Unit,
) {
    require(columns >= 1) { "Required columns >= 1, received columns = $columns" }

    val scope = GridScopeImpl(columns).apply { content() }
    val rows = scope.spans.chunkedBy(columns)

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = horizontalArrangement,
                verticalAlignment = verticalAlignment,
            ) {
                row.forEach { span ->
                    Box(modifier = Modifier.weight(span.columns.toFloat(), fill = false)) {
                        span.content()
                    }
                }
            }
        }
    }
}

private fun Iterable<Span>.chunkedBy(columns: Int): List<List<Span>> = buildList {
    val spans = this@chunkedBy
    var row = mutableListOf<Span>()

    var acc = 0
    for (span in spans) {
        val requiresNewRow = acc + span.columns > columns

        if (requiresNewRow && row.isNotEmpty()) {
            // Complete the rest of the row.
            val remaining = columns - acc
            if (remaining > 0) row.add(Span.empty(remaining))

            // Commit the row.
            add(row)

            // Move on to a new row.
            row = mutableListOf()
            acc = 0
        }

        // Add span to row.
        row.add(span)
        acc += span.columns
    }

    if (row.isNotEmpty()) {
        // Complete the rest of the row.
        val remaining = columns - acc
        if (remaining > 0) row.add(Span.empty(remaining))

        // Commit the row.
        add(row)
    }
}

/**
 * Scope used by [Grid] to define its children.
 *
 * Within this scope, you can add items that span one or more columns, or insert a full-width line
 * (e.g., for separators). Items are placed into rows in the order they are declared.
 *
 * ### Example
 *
 * ```
 * Grid(columns = 3) {
 *     item { Text("One") }
 *     item(columns = 2) { Text("Two spans 2 columns") }
 *     line { Divider() }
 *     item { Text("Three") }
 * }
 * ```
 */
interface GridScope {

    /**
     * Adds an item to the grid that spans the given number of columns.
     *
     * @param columns The number of columns this item should span. Must be ≥ 1 and ≤ the total
     *   column count of the parent [Grid]. Defaults to 1.
     * @param content The composable content of this grid cell.
     *
     * ### Example
     *
     * ```
     * Grid(columns = 4) {
     *     item(columns = 2) { Text("Half-width item") }
     *     item { Text("Quarter-width") }
     *     item { Text("Quarter-width") }
     * }
     * ```
     */
    fun item(@IntRange(from = 1) columns: Int = 1, content: @Composable () -> Unit)

    /**
     * Adds a full-width item that occupies the entire row.
     *
     * This is useful for inserting horizontal dividers, section headers, or any content that should
     * not share space with other grid items.
     *
     * @param content The composable content to render across the full row.
     *
     * ### Example
     *
     * ```
     * Grid(columns = 3) {
     *     item { Text("Item A") }
     *     item { Text("Item B") }
     *     line { Divider() } // spans all 3 columns
     *     item { Text("Item C") }
     * }
     * ```
     */
    fun line(content: @Composable () -> Unit)
}

private data class Span(val columns: Int, val content: @Composable () -> Unit) {
    companion object {
        fun empty(remaining: Int): Span =
            Span(columns = remaining, content = { Box(modifier = Modifier.fillMaxWidth()) })
    }
}

private class GridScopeImpl(val maxColumns: Int) : GridScope {

    private val _spans = mutableListOf<Span>()
    val spans: List<Span> = _spans

    override fun item(columns: Int, content: @Composable (() -> Unit)) {
        require(columns >= 1) { "Required columns >= 1, received columns = $columns" }
        require(columns <= maxColumns) {
            "Required columns <= $maxColumns, received columns = $columns"
        }

        _spans += Span(columns, content)
    }

    override fun line(content: @Composable (() -> Unit)) {
        item(columns = maxColumns, content)
    }
}
