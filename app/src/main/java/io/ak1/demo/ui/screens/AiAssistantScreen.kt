package io.ak1.demo.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.ak1.demo.R
import io.ak1.demo.presentation.assistant.AiAssistantEvent
import io.ak1.demo.presentation.assistant.AiAssistantIntent
import io.ak1.demo.presentation.assistant.AiAssistantViewModel
import io.ak1.demo.presentation.assistant.getDate
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import io.ak1.demo.ui.components.ChatBlock
import io.ak1.demo.ui.components.Messages
import io.ak1.demo.ui.components.UserInput
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.commonmark.node.Text
import org.koin.compose.koinInject


/**
 * AI Assistant Screen composable that provides an interactive chat interface.
 * 
 * This screen manages the AI conversation UI with features including:
 * - Real-time message display with streaming responses
 * - Voice recognition for hands-free input
 * - Automatic scrolling to latest messages
 * - Error handling and user feedback
 * 
 * The screen integrates with both PDF viewer state for document context
 * and AI assistant state for conversation management.
 * 
 * @param pdfViewerViewModel ViewModel managing PDF viewer state and AI initialization
 * @param viewModel AI Assistant ViewModel handling chat interactions and voice recognition
 */
@Composable
fun AiAssistantScreen(
    pdfViewerViewModel: PdfViewerViewModel,
    viewModel: AiAssistantViewModel = koinInject<AiAssistantViewModel>()
) {
    val state by viewModel.state.collectAsState()
    val pdfState by pdfViewerViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState(Int.MAX_VALUE, Int.MAX_VALUE)
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(pdfState.isAiAssistantInitialized) {
        if (pdfState.isAiAssistantInitialized) {
            scope.launch {
                viewModel.startListening()
            }

        }
    }
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AiAssistantEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is AiAssistantEvent.MessageSent -> {
                    focusManager.clearFocus()
                }

                is AiAssistantEvent.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        scrollState.requestScrollToItem(Int.MAX_VALUE,Int.MAX_VALUE)
                    }
                }
            }
        }
    }
    Box(
        Modifier.fillMaxSize()
    ) {

        Column(
            Modifier
                .imePadding()
                .fillMaxSize()
                .then(if (state.isRecording) Modifier.blur(12.dp) else Modifier)
        ) {
            // Display voice recognition error if any
            state.error?.let {
                Text(it)
            }

            // Messages list
            Messages(
                messages = state.messages.toImmutableList(),
                navigateToProfile = { /* No-op or handle navigation */ },
                modifier = Modifier.weight(1f),
                scrollState = scrollState
            ) {
                viewModel.sendMessage(it)
            }
            
            // Show typing indicator when AI is loading
            if (state.isLoading) {
                TypingIndicator()
            }

            // User input with voice recording capabilities
            UserInput(textFieldValue = state.inputText, onTextChanged = {
                viewModel.processIntent(AiAssistantIntent.UpdateInputText(it))
            }, onMessageSent = { message ->
                viewModel.processIntent(AiAssistantIntent.SendMessage(message))
            }, onRecordingIconCLicked = {
                viewModel.processIntent(AiAssistantIntent.StartRecording)
            }, resetScroll = {
                scope.launch {
                    scrollState.requestScrollToItem(Int.MAX_VALUE)
                }
            })
        }

        if (state.isRecording) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text(
                    text = state.partialRecordingText,
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Typing indicator composable that shows animated dots when AI is processing.
 * 
 * Displays an AI avatar with three animated dots to indicate that the AI
 * is thinking or processing a response. Uses infinite animations for
 * smooth visual feedback.
 */
@Composable
fun TypingIndicator() {

    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        // Avatar
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(42.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.tertiary, CircleShape)
                .clip(CircleShape)
                .align(Alignment.Top)
                .padding(6.dp),
            contentScale = ContentScale.Inside,
            painter = painterResource(id = R.drawable.bot),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )

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
                    text = "Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .alignBy(LastBaseline)
                        .paddingFrom(LastBaseline, after = 8.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))


            }
            Row(
                modifier = Modifier.padding(16.dp, 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing_animation")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                                CircleShape
                            )
                    )
                }
            }


        }
    }
}
