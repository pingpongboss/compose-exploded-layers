package io.github.pingpongboss.explodedlayers.samples.android

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.SeparateLayer

@Composable
fun DialerApp() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Body(modifier = Modifier.fillMaxWidth().weight(1f))
        Footer(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun Body(modifier: Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(Modifier.height(16.dp))

        Text(text = "Calling...")
        Text(text = "Coppelia", style = MaterialTheme.typography.displayMedium)
        Text(text = "+1 212-858-5001")

        MagicCue()
    }
}

@Composable
fun MagicCue() {
    val scrollState = rememberScrollState()

    SeparateLayer {
        val shape = RoundedCornerShape(16.dp)
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .clip(shape)
                    .border(width = 1.dp, color = Color.Yellow, shape = shape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier.clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.PhonelinkLock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(text = "Magic Cue", style = MaterialTheme.typography.labelMedium)
            }

            Section()
            Section()
            Section()
        }
    }
}

@Composable
private fun Section() {
    Column(
        modifier =
            Modifier.clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Your reservation", style = MaterialTheme.typography.headlineMedium)
        ReservationGrid()
        Text(text = "Was this result helpful?", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ReservationGrid() {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        InfoCell(
            header = "Date & time",
            body = "Aug 20, 2025, 7:00 PM",
            modifier = Modifier.weight(2f).fillMaxHeight(),
        )
        InfoCell(header = "Party size", body = "2", modifier = Modifier.weight(1f).fillMaxHeight())
    }
}

@Composable
fun InfoCell(header: String, body: String, modifier: Modifier) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
    ) {
        Text(text = header, style = MaterialTheme.typography.labelMedium)
        Text(text = body, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun Footer(modifier: Modifier) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ButtonRow()
        EndCallButton()
    }
}

@Composable
fun ButtonRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ButtonWithLabel(
            icon = Icons.Default.Keyboard,
            label = "Keypad",
            modifier = Modifier.weight(1f),
        )
        ButtonWithLabel(icon = Icons.Default.Mic, label = "Mute", modifier = Modifier.weight(1f))
        ButtonWithLabel(
            icon = Icons.Default.Speaker,
            label = "Speaker",
            modifier = Modifier.weight(1f),
        )
        ButtonWithLabel(
            icon = Icons.Default.MoreVert,
            label = "More",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun ButtonWithLabel(icon: ImageVector, label: String, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilledTonalIconButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(64.dp)) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(28.dp))
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun EndCallButton() {
    FilledTonalIconButton(
        onClick = {},
        modifier = Modifier.width(160.dp).height(64.dp),
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Red,
                contentColor = Color.Black,
            ),
    ) {
        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "End Call")
    }
}

@Preview(showBackground = true)
@Composable
fun DialerAppPreview() {
    DialerApp()
}
