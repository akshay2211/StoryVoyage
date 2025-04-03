package io.ak1.demo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.ak1.demo.R
import io.ak1.demo.presentation.assistant.getDate
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.Issuer
import io.nutrient.data.models.Issuer.Companion.value
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val ConversationTestTag = "ConversationTestTag"
private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun Messages(
    messages: List<CompletionResponse>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        LazyColumn(
            state = scrollState, modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {
            items(
                items = messages.withDateDividers()
            ) { item ->
                when (item) {
                    is ChatListItem.DateDivider -> DayHeader(dayString = formatDateDivider(item.date))
                    is ChatListItem.MessageItem -> {
                        if (item.message.content.isNullOrEmpty()) return@items
                        MessageItem(message = item.message, navigateToProfile = navigateToProfile, onClick = onClick)
                    }
                }
            }
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                // Check if we're NOT at the bottom of the list
                // In a standard layout, the last item index is (itemCount - 1)
                val lastVisibleItem =
                    scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = scrollState.layoutInfo.totalItemsCount

                // Show button if we're not seeing the last item (bottom of the list)
                totalItems > 0 && lastVisibleItem < totalItems - 1
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled, onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(Int.MAX_VALUE)
                }
            }, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MessageItem(
    message: CompletionResponse, navigateToProfile: (String) -> Unit,
    onClick: (String) -> Unit = {}
) {
    val isUserMessage = message.sender == Issuer.HUMAN.value()
    val borderColor = if (isUserMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    CompositionLocalProvider(LocalLayoutDirection provides if (isUserMessage) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = { navigateToProfile("author") }) // replace with actual author ID
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
//                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Top)
                    .padding(6.dp),
                contentScale = ContentScale.Inside,
                painter = painterResource(id = if (isUserMessage) R.drawable.user_round else R.drawable.bot),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    if (isUserMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                )
            )
//            } ?: Spacer(modifier = Modifier.width(74.dp))

            // Message content
            Column(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
            ) {
                // Author and timestamp
                Row(
                    modifier = Modifier.semantics(mergeDescendants = true) {}) {
                    Text(
                        text = if (isUserMessage) "You" else "Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .alignBy(LastBaseline)
                            .paddingFrom(LastBaseline, after = 8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = message.getDate(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alignBy(LastBaseline),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Message bubble
                ChatBubble(
                    message = message, isUserMessage = isUserMessage,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: CompletionResponse, isUserMessage: Boolean,
    onClick: (String) -> Unit = {}
) {
    val backgroundBubbleColor = if (isUserMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isUserMessage) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundBubbleColor,
        shape = ChatBubbleShape,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

            Column {
                MarkdownText(
                    markdown = message.content ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Start
                )
                message.suggestions?.let {
                    it.forEach {
                        OutlinedButton(
                            {onClick.invoke(it.text)},
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(it.text)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    HorizontalDivider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

// Helper function to format date dividers
fun formatDateDivider(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when {
        date.isEqual(today) -> "Today"
        date.isEqual(yesterday) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
    }
}


sealed class ChatListItem {
    data class MessageItem(val message: CompletionResponse) : ChatListItem()
    data class DateDivider(val date: LocalDate) : ChatListItem()
}

// Function to convert a list of messages to a list with date dividers
fun List<CompletionResponse>.withDateDividers(): List<ChatListItem> {
    if (isEmpty()) return emptyList()

    // Handle messages with timestamp 0 by replacing with current time
    val currentTimestamp = System.currentTimeMillis()
    val processedMessages = map { message ->
        if (message.timestamp == 0L) {
            message.copy(timestamp = currentTimestamp)
        } else {
            message
        }
    }

    // Sort messages by timestamp (oldest first)
    val sortedMessages = processedMessages.sortedBy { it.timestamp }

    val items = mutableListOf<ChatListItem>()
    var currentDate: LocalDate? = null

    for (message in sortedMessages) {
        val messageDate =
            Instant.ofEpochMilli(message.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

        // Add a date divider if we've moved to a new date
        if (currentDate == null || messageDate != currentDate) {
            items.add(ChatListItem.DateDivider(messageDate))
            currentDate = messageDate
        }

        items.add(ChatListItem.MessageItem(message))
    }

    return items
}