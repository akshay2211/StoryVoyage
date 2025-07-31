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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.ak1.demo.R
import io.ak1.demo.presentation.assistant.getDate
import io.nutrient.data.models.AiAssistantEvents
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.Issuer
import io.nutrient.data.models.Issuer.Companion.value
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val ConversationTestTag = "ConversationTestTag"
private val ChatBlockShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun Messages(
    messages: ImmutableList<CompletionResponse>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val canScrollForward by remember(messages) {
        derivedStateOf { scrollState.canScrollForward }
    }

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
                        key(item.message.index) {
                        if (item.message.content.isNullOrEmpty()) return@items
                        MessageItem(message = item.message, navigateToProfile = navigateToProfile,
                            onSizeChange = { it ->
                                if (!item.message.end) {
                                    scrollState.scrollToItem(messages.lastIndex, it.height)
                                }
                            },
                            onClick = onClick)
                    }}
                }
            }
        }

        JumpToBottom(
            enabled = canScrollForward, onClicked = {
                scope.launch {
                        scrollState.animateScrollToItem(messages.lastIndex, Int.MAX_VALUE)
                }
            }, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MessageItem(
    message: CompletionResponse, navigateToProfile: (String) -> Unit,
    onSizeChange : suspend (IntSize) -> Unit = {},
    onClick: (String) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
   val isUserMessage = message.sender == Issuer.HUMAN.value()
    val borderColor = if (isUserMessage) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
    var size by remember { mutableStateOf(IntSize.Zero) }

    CompositionLocalProvider(LocalLayoutDirection provides if (isUserMessage) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            // Avatar
            Image(
                modifier = Modifier
                    .clickable(onClick = { navigateToProfile("author") }) // replace with actual author ID
                    .padding(horizontal = 16.dp)
                    .size(42.dp)
                    .border(1.5.dp, borderColor, CircleShape)
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

            // Message content
            Column(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f)
                    .onGloballyPositioned{ it ->
                        if (size == it.size) return@onGloballyPositioned
                        size = it.size
                        scope.launch { onSizeChange.invoke(size) }
                    }
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

                // Message Block
                ChatBlock(
                    message = message, isUserMessage = isUserMessage,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
fun ChatBlock(
    message: CompletionResponse, isUserMessage: Boolean,
    onClick: (String) -> Unit = {}
) {
    val backgroundBlockColor = if (isUserMessage) {
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
        color = backgroundBlockColor,
        shape = ChatBlockShape,
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
                message.suggestions?.let { suggestions ->
                    suggestions.forEach { suggestion ->
                        OutlinedButton(
                            {onClick.invoke(suggestion.text)},
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(suggestion.text)
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

// Preview functions
@Preview(showBackground = true)
@Composable
fun MessagesPreview() {
    val sampleMessages = persistentListOf(
        CompletionResponse(
            content = "Hello! How can I help you today?",
            sender = Issuer.AI.value(),
            timestamp = System.currentTimeMillis() - 60000,
            state = AiAssistantEvents.Success,
            end = true,
            suggestions = null
        ),
        CompletionResponse(
            content = "I need help with my latest project.",
            sender = Issuer.HUMAN.value(),
            timestamp = System.currentTimeMillis(),
            state = AiAssistantEvents.Success,
            end = true
        )
    )
    Messages(
        messages = sampleMessages,
        navigateToProfile = {},
        scrollState = LazyListState()
    )
}

@Preview(showBackground = true)
@Composable
fun MessageItemPreview() {
    val userMessage = CompletionResponse(
        content = "This is a user message with some longer content to show how it looks when wrapped.",
        sender = Issuer.HUMAN.value(),
        timestamp = System.currentTimeMillis(),
        state = AiAssistantEvents.Success,
        end = true,
        suggestions = null
    )
    MessageItem(
        message = userMessage,
        navigateToProfile = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MessageItemAssistantPreview() {
    val assistantMessage = CompletionResponse(
        content = "This is an assistant message with some helpful information and suggestions.",
        sender = Issuer.AI.value(),
        timestamp = System.currentTimeMillis(),
        state = AiAssistantEvents.Success,
        end = true,
    )
    MessageItem(
        message = assistantMessage,
        navigateToProfile = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChatBlockPreview() {
    val message = CompletionResponse(
        content = "This is a sample chat message with **markdown** support.",
        sender = Issuer.HUMAN.value(),
        timestamp = System.currentTimeMillis(),
        state = AiAssistantEvents.Success,
        end = true,
    )
    ChatBlock(
        message = message,
        isUserMessage = true
    )
}

@Preview(showBackground = true)
@Composable
fun ChatBlockAssistantPreview() {
    val message = CompletionResponse(
        content = "This is an assistant response with suggestions below.",
        sender = Issuer.AI.value(),
        timestamp = System.currentTimeMillis(),
        state = AiAssistantEvents.Success,
        end = true,
    )
    ChatBlock(
        message = message,
        isUserMessage = false
    )
}

@Preview(showBackground = true)
@Composable
fun DayHeaderPreview() {
    DayHeader(dayString = "Today")
}

@Preview(showBackground = true)
@Composable
fun DayHeaderYesterdayPreview() {
    DayHeader(dayString = "Yesterday")
}

@Preview(showBackground = true)
@Composable
fun DayHeaderDatePreview() {
    DayHeader(dayString = "July 3, 2025")
}