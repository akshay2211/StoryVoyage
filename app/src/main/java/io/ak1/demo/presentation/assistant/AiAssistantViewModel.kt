package io.ak1.demo.presentation.assistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.VoiceRecognitionState
import io.ak1.demo.domain.usecase.InitializeAiAssistantUseCase
import io.ak1.demo.domain.usecase.SendMessageUseCase
import io.ak1.demo.domain.usecase.TerminateAiAssistantUseCase
import io.ak1.demo.domain.usecase.voice.CancelVoiceRecognitionUseCase
import io.ak1.demo.domain.usecase.voice.StartVoiceRecognitionUseCase
import io.ak1.demo.domain.usecase.voice.StopVoiceRecognitionUseCase
import io.nutrient.data.models.AiAssistantEvents
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.Issuer
import io.nutrient.data.models.Issuer.Companion.value
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiAssistantViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val initializeAiAssistantUseCase: InitializeAiAssistantUseCase,
    private val terminateAiAssistantUseCase: TerminateAiAssistantUseCase,
    private val startVoiceRecognitionUseCase: StartVoiceRecognitionUseCase,
    private val stopVoiceRecognitionUseCase: StopVoiceRecognitionUseCase,
    private val cancelVoiceRecognitionUseCase: CancelVoiceRecognitionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiAssistantState())
    val state: StateFlow<AiAssistantState> = _state.asStateFlow()

    private val _events = Channel<AiAssistantEvent>()
    val events = _events.receiveAsFlow()

    // Collect voice recognition state and AI responses
    init {
        viewModelScope.launch {
            startVoiceRecognitionUseCase.voiceState.collectLatest { voiceState ->
                updateVoiceState(voiceState)
            }
        }
    }

    fun processIntent(intent: AiAssistantIntent) {
        when (intent) {
            is AiAssistantIntent.SendMessage -> sendMessage(intent.message)
            is AiAssistantIntent.StartRecording -> startRecording()
            is AiAssistantIntent.StopRecording -> stopRecording()
            is AiAssistantIntent.CancelRecording -> cancelRecording()
            is AiAssistantIntent.UpdateInputText -> updateInputText(intent.text)
        }
    }

    private fun updateInputText(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            // Clear input
            _state.update { it.copy(inputText = "", isLoading = true) }

            // Trigger scroll to bottom event
            _events.send(AiAssistantEvent.ScrollToBottom)

            try {
                // Get document ID from current conversation or use a default
                val documentId = _state.value.currentDocumentId ?: "default"

                // Send message to AI Assistant
                sendMessageUseCase.execute(message, documentId)

                // The response will be handled by the flow collector in init
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Failed to send message: ${e.message}"))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleAiResponse(response: CompletionResponse) {
        viewModelScope.launch {
            when (response.state) {

                is AiAssistantEvents.Chat -> {
                    _state.update { currentState ->
                        // Check if this is a new message or if we need to append to an existing one
                        if (response.sender.isEmpty() && (currentState.messages.isEmpty() || currentState.messages.lastOrNull()?.state !is AiAssistantEvents.Chat) && !response.end) {
                            // This is a new message - add it to the list
                            currentState.copy(
                                isLoading = false, messages = currentState.messages + response
                            )
                        } else if (response.end && response.sender == Issuer.AI.value()) {
                            // This is the final part of a message - mark it complete
                            val updatedMessages = currentState.messages.toMutableList()
                            if (updatedMessages.isNotEmpty()) {
                                val lastIndex = updatedMessages.lastIndex
                                updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                    end = true
                                )
                            }
                            currentState.copy(
                                isLoading = false, messages = updatedMessages
                            )
                        } else {
                            // This is a continuation of the current message - append the content
                            val updatedMessages = currentState.messages.toMutableList()
                            if (updatedMessages.isNotEmpty()) {
                                val lastIndex = updatedMessages.lastIndex
                                updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                    content = updatedMessages[lastIndex].content + response.content
                                )
                            }
                            currentState.copy(
                                isLoading = false, messages = updatedMessages
                            )
                        }
                    }

                    _events.send(AiAssistantEvent.MessageSent)
                    _events.send(AiAssistantEvent.ScrollToBottom)
                    _state.update { it.copy(isLoading = false) }
                }

                is AiAssistantEvents.Success -> {
                    if (response.content.isNullOrEmpty()) return@launch
                    addMessage(response)
                    _events.send(AiAssistantEvent.MessageSent)
                    _events.send(AiAssistantEvent.ScrollToBottom)
                    _state.update { it.copy(isLoading = false) }
                }

                is AiAssistantEvents.Loading -> {
                    addMessage(response)
                    _events.send(AiAssistantEvent.MessageSent)
                    _events.send(AiAssistantEvent.ScrollToBottom)
                    _state.update { it.copy(isLoading = true) }
                }

                else -> {}
            }

        }
    }

    private fun startRecording() {
        viewModelScope.launch {
            try {
                startVoiceRecognitionUseCase.execute()
                _state.update { it.copy(isRecording = true) }
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Error starting voice recording: ${e.message}"))
            }
        }
    }

    private fun stopRecording() {
        viewModelScope.launch {
            try {
                val recognizedText = stopVoiceRecognitionUseCase.execute()
                _state.update {
                    it.copy(
                        inputText = recognizedText, isRecording = false, partialRecordingText = ""
                    )
                }
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Error stopping voice recording: ${e.message}"))
                _state.update { it.copy(isRecording = false, partialRecordingText = "") }
            }
        }
    }

    private fun cancelRecording() {
        viewModelScope.launch {
            try {
                cancelVoiceRecognitionUseCase.execute()
                _state.update {
                    it.copy(
                        isRecording = false, partialRecordingText = ""
                    )
                }
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Error canceling voice recording: ${e.message}"))
                _state.update { it.copy(isRecording = false, partialRecordingText = "") }
            }
        }
    }

    private fun updateVoiceState(voiceState: VoiceRecognitionState) {
        _state.update {
            it.copy(
                partialRecordingText = voiceState.partialText, isRecording = voiceState.isListening
            )
        }

        voiceState.error?.let { error ->
            viewModelScope.launch {
                _events.send(AiAssistantEvent.Error(error))
            }
        }
    }

    private fun addMessage(message: CompletionResponse) {
        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + message
            )
        }
    }

    fun startListening() {
        Log.e("Akshay", "startListening: ")
        viewModelScope.launch {
            sendMessageUseCase.getResponseStream().collectLatest { response ->
                if (response == null) {
                    Log.e("AiAssistantViewModel", "Response is null, handleAiResponse not called")
                } else {
                    handleAiResponse(response)
                }
            }
        }
    }


}

fun CompletionResponse.getDate(): String {
    val formatter = SimpleDateFormat("HH:mm aa", Locale.getDefault())
    return formatter.format(Date(this.timestamp))
}