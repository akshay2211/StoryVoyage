package io.ak1.demo.presentation.assistant

import io.nutrient.data.models.CompletionResponse

/**
 * Represents the UI state for the AI Assistant feature
 */
data class AiAssistantState(
    val isLoading: Boolean = false,
    val messages: List<CompletionResponse> = emptyList(),
    val error: String? = null,
    val inputText: String = "",
    val isRecording: Boolean = false,
    val partialRecordingText: String = "",
    val currentDocumentId: String? = null,
    val isInitialized: Boolean = false
)

/**
 * Sealed interface representing user intents for the AI Assistant
 */
sealed interface AiAssistantIntent {
    data class SendMessage(val message: String) : AiAssistantIntent
    object StartRecording : AiAssistantIntent
    object StopRecording : AiAssistantIntent
    object CancelRecording : AiAssistantIntent
    data class UpdateInputText(val text: String) : AiAssistantIntent
}

/**
 * Sealed interface representing one-time events for the AI Assistant UI
 */
sealed interface AiAssistantEvent {
    data class Error(val message: String) : AiAssistantEvent
    object MessageSent : AiAssistantEvent
    object ScrollToBottom : AiAssistantEvent
}