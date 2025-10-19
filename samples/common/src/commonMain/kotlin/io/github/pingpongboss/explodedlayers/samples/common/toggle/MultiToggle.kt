package io.github.pingpongboss.explodedlayers.samples.common.toggle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MultiToggle(
    options: List<String>,
    current: String,
    onSelectionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(IntrinsicSize.Min)
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline))
    ) {
        options.forEachIndexed { index, option ->
            val selected = option == current
            if (index != 0) {
                Divider(
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxHeight().width(1.dp),
                )
            }
            Box(
                modifier =
                    Modifier.weight(1f)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .toggleable(
                            value = selected,
                            onValueChange = { if (it) onSelectionChange(option) },
                        )
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option,
                    color =
                        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}
