package io.github.pingpongboss.explodedlayers.samples.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.pingpongboss.explodedlayers.SeparateLayer
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.MessagesItem
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.MessagesItem.MessageItem
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.SectionFooterItem
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.SectionHeaderItem
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.SuggestionsItem
import io.github.pingpongboss.explodedlayers.samples.android.ConversationItem.SuggestionsItem.MagicCueSuggestionItem
import io.github.pingpongboss.explodedlayers.samples.android.theme.ExplodedLayersSampleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MessagesApp() {
    Column {
        Header(modifier = Modifier.fillMaxWidth())
        Body(modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Header(modifier: Modifier) {
    TopAppBar(
        title = { Text(text = "Hannah Cho", style = MaterialTheme.typography.titleMedium) },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Default.Call, contentDescription = "Call") }
            IconButton(onClick = {}) { Icon(Icons.Default.Videocam, contentDescription = "Video") }
            IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = "Menu") }
        },
        windowInsets = WindowInsets(),
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
    )
}

@Composable
private fun Body(modifier: Modifier) {
    Box(modifier = modifier) {
        var footerSize: IntSize? by remember { mutableStateOf(null) }

        ConversationList(
            modifier =
                Modifier.fillMaxWidth().padding(horizontal = 8.dp).align(Alignment.BottomCenter),
            footerHeight = footerSize?.height,
        )
        Footer(
            modifier =
                Modifier.fillMaxWidth()
                    .onGloballyPositioned { footerSize = it.size }
                    .align(Alignment.BottomCenter)
        )
    }
}

sealed interface ConversationItem {

    data class SectionHeaderItem(val label: String) : ConversationItem

    data class MessagesItem(val messages: List<MessageItem>, val received: Boolean) :
        ConversationItem {
        data class MessageItem(val label: String)
    }

    data class SectionFooterItem(val label: String, val received: Boolean) : ConversationItem

    data class SuggestionsItem(val suggestions: List<MagicCueSuggestionItem>) : ConversationItem {
        data class MagicCueSuggestionItem(val label: String, val attribution: String)
    }
}

private val conversationItems =
    mutableStateListOf(
        SectionHeaderItem(label = "Sunday, Jul 7 - 2:15 PM"),
        MessagesItem(
            messages = listOf(MessageItem(label = "When are you visiting again?")),
            received = true,
        ),
        MessagesItem(
            messages =
                listOf(
                    MessageItem(label = "Next week!"),
                    MessageItem(label = "Lol can't believe you forgot"),
                ),
            received = false,
        ),
        MessagesItem(
            messages =
                listOf(
                    MessageItem(label = "I so did not"),
                    MessageItem(label = "Let's grab dinner while you're here?"),
                ),
            received = true,
        ),
        MessagesItem(
            messages = listOf(MessageItem(label = "I'll look for something")),
            received = false,
        ),
        SectionHeaderItem(label = "Tuesday, Jul 29 - 7:30 PM"),
        MessagesItem(messages = listOf(MessageItem(label = "Yo!")), received = false),
        MessagesItem(
            messages =
                listOf(
                    MessageItem(label = "Hey, excited for your trip to New York!"),
                    MessageItem(label = "Where is the dinner reservation?"),
                ),
            received = true,
        ),
        SectionFooterItem(label = "7:31 PM", received = true),
        SuggestionsItem(
            suggestions =
                listOf(
                    MagicCueSuggestionItem(
                        label = "207 W 14th St, New York, NY",
                        attribution = "Reservation at Coppelia",
                    ),
                    MagicCueSuggestionItem(
                        label = "Coppelia",
                        attribution = "Reservation at Coppelia",
                    ),
                )
        ),
    )

@Composable
private fun ConversationList(modifier: Modifier, footerHeight: Int?) {
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) { scrollState.scrollTo(scrollState.maxValue) }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            val scope = rememberCoroutineScope()
            for (item in conversationItems) {
                when (item) {
                    is SectionHeaderItem -> {
                        Text(
                            text = item.label,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    is MessagesItem -> {
                        MessagesSection(
                            messages = item.messages,
                            received = item.received,
                            modifier =
                                Modifier.align(
                                    if (item.received) Alignment.Start else Alignment.End
                                ),
                        )
                    }
                    is SectionFooterItem -> {
                        Text(
                            text = item.label,
                            modifier =
                                Modifier.align(
                                    if (item.received) Alignment.Start else Alignment.End
                                ),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    is SuggestionsItem -> {
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(500.milliseconds)
                            visible = true
                        }
                        AnimatedVisibility(visible = visible) {
                            SuggestionsRow(
                                suggestions = item.suggestions,
                                modifier = Modifier.align(Alignment.End),
                                onSuggestionClicked = { suggestion ->
                                    scope.launch {
                                        // Hide the suggestions.
                                        val suggestions = conversationItems.removeLast()

                                        // Append the clicked suggestion.
                                        val last = conversationItems.last()
                                        val messages =
                                            if (last is MessagesItem && !last.received) {
                                                conversationItems.removeLast()
                                                last.messages
                                            } else {
                                                emptyList()
                                            }
                                        conversationItems +=
                                            MessagesItem(
                                                messages =
                                                    messages + MessageItem(label = suggestion),
                                                received = false,
                                            )

                                        // Show suggestions again.
                                        delay(500.milliseconds)
                                        conversationItems += suggestions
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        with(LocalDensity.current) {
            footerHeight?.let { Spacer(modifier = Modifier.height(it.toDp())) }
        }
    }
}

@Composable
private fun MessagesSection(messages: List<MessageItem>, received: Boolean, modifier: Modifier) {
    Column(
        modifier =
            modifier.clip(
                if (messages.size == 1) {
                    RoundedCornerShape(50)
                } else {
                    RoundedCornerShape(16.dp)
                }
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (message in messages) {
            Text(
                text = message.label,
                modifier =
                    modifier
                        .clip(
                            if (received) {
                                RoundedCornerShape(
                                    topStart = CornerSize(4.dp),
                                    bottomStart = CornerSize(4.dp),
                                    topEnd = CornerSize(50),
                                    bottomEnd = CornerSize(50),
                                )
                            } else {
                                RoundedCornerShape(
                                    topStart = CornerSize(50),
                                    bottomStart = CornerSize(50),
                                    topEnd = CornerSize(4.dp),
                                    bottomEnd = CornerSize(4.dp),
                                )
                            }
                        )
                        .background(
                            color =
                                if (received) {
                                    MaterialTheme.colorScheme.surfaceContainer
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                color =
                    if (received) {
                        Color.Unspecified
                    } else {
                        Color.White
                    },
            )
        }
    }
}

@Composable
private fun SuggestionsRow(
    suggestions: List<MagicCueSuggestionItem>,
    modifier: Modifier,
    onSuggestionClicked: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    SeparateLayer {
        Row(
            modifier = modifier.horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (suggestion in suggestions) {
                MagicCueChip(suggestion = suggestion, onSuggestionClicked = onSuggestionClicked)
            }
        }
    }
}

@Composable
private fun MagicCueChip(
    suggestion: MagicCueSuggestionItem,
    onSuggestionClicked: (String) -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier =
            Modifier.clip(shape)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                .background(color = MaterialTheme.colorScheme.surface)
                .clickable { onSuggestionClicked(suggestion.label) }
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.PhonelinkLock,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = suggestion.label, style = MaterialTheme.typography.bodyLarge)
            Text(text = suggestion.attribution, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun Footer(modifier: Modifier) {
    Row(
        modifier =
            modifier
                .height(IntrinsicSize.Max)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = .98f))
                .padding(top = 8.dp, bottom = 16.dp)
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        EditField(modifier = Modifier.weight(1f).fillMaxHeight())
        OutlinedButton(
            imageVector = Icons.Outlined.Mic,
            contentDescription = "Add audio",
            modifier = Modifier.wrapContentWidth().fillMaxHeight(),
        )
    }
}

@Composable
private fun EditField(modifier: Modifier) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EditFieldButton(
            imageVector = Icons.Outlined.AddCircleOutline,
            contentDescription = "Add file",
        )
        Text(text = "RCS message", modifier = Modifier.weight(1f))
        EditFieldButton(imageVector = Icons.Outlined.EmojiEmotions, contentDescription = "Emoji")
        EditFieldButton(imageVector = Icons.Outlined.Photo, contentDescription = "Add photo")
    }
}

@Composable
private fun EditFieldButton(imageVector: ImageVector, contentDescription: String) {
    Icon(imageVector = imageVector, contentDescription = contentDescription)
}

@Composable
private fun OutlinedButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier,
) {
    FilledTonalIconButton(
        onClick = {},
        modifier = modifier.aspectRatio(1f),
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesAppPreview() {
    ExplodedLayersSampleTheme { MessagesApp() }
}
