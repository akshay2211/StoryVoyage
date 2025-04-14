package io.ak1.demo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.ak1.demo.presentation.assistant.AiAssistantEvent
import io.ak1.demo.presentation.assistant.AiAssistantIntent
import io.ak1.demo.presentation.assistant.AiAssistantViewModel
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import io.ak1.demo.ui.components.Messages
import io.ak1.demo.ui.components.UserInput
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.commonmark.node.Text
import org.koin.compose.koinInject


@Composable
fun AiAssistantScreen(
    pdfViewerViewModel: PdfViewerViewModel,
    viewModel: AiAssistantViewModel = koinInject<AiAssistantViewModel>()
) {
    val state by viewModel.state.collectAsState()
    val pdfState by pdfViewerViewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

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
                    // Handle message sent confirmation if needed
                }

                is AiAssistantEvent.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        scrollState.animateScrollToItem(Int.MAX_VALUE)
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

            // Display partial text during recording
            if (state.isRecording) {
                Text(state.partialRecordingText)
            }

            // Messages list
            Messages(
                messages = state.messages,
                navigateToProfile = { /* No-op or handle navigation */ },
                modifier = Modifier.weight(1f),
                scrollState = scrollState
            ) {
                viewModel.sendMessage(it)
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
                    scrollState.animateScrollToItem(Int.MAX_VALUE)
                }
            })
        }

        if (state.isRecording) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text(
                    text = state.partialRecordingText,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
