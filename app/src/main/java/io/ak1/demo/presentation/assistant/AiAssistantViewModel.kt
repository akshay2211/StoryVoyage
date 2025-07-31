package io.ak1.demo.presentation.assistant

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.VoiceRecognitionState
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.ak1.demo.domain.repository.VoiceRecognitionRepository
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

/**
 * ViewModel for managing AI Assistant interactions and voice recognition functionality.
 * 
 * This ViewModel implements the MVI (Model-View-Intent) pattern for handling:
 * - AI chat conversations with streaming responses
 * - Voice recognition for hands-free interaction
 * - Message state management and UI events
 * - Real-time response handling from AI service
 * 
 * @param aiAssistantRepository Repository for AI Assistant operations
 * @param voiceRecognitionRepository Repository for voice recognition operations
 */
class AiAssistantViewModel(
    private val aiAssistantRepository: AiAssistantRepository,
    private val voiceRecognitionRepository: VoiceRecognitionRepository
) : ViewModel() {

    /** Mutable state flow for managing AI Assistant UI state */
    private val _state = MutableStateFlow(AiAssistantState())
    
    /** Exposed read-only state flow for UI observation */
    val state: StateFlow<AiAssistantState> = _state.asStateFlow()

    /**
     * Cleanup method called when ViewModel is destroyed.
     * Resets the state to prevent memory leaks.
     */
    override fun onCleared() {
        super.onCleared()
        _state.update { AiAssistantState() }
    }

    /** Channel for one-time UI events */
    private val _events = Channel<AiAssistantEvent>()
    
    /** Exposed flow for UI events like errors and navigation */
    val events = _events.receiveAsFlow()

    /**
     * Initializes voice recognition state collection.
     * Automatically updates UI state when voice recognition state changes.
     */
    init {
        viewModelScope.launch {
            voiceRecognitionRepository.voiceState.collectLatest { voiceState ->
                updateVoiceState(voiceState)
            }
        }
    }

    /**
     * Processes user intents using the MVI pattern.
     * Routes different types of user actions to appropriate handler methods.
     * 
     * @param intent The user intent to process
     */
    fun processIntent(intent: AiAssistantIntent) {
        when (intent) {
            is AiAssistantIntent.SendMessage -> sendMessage(intent.message)
            is AiAssistantIntent.StartRecording -> startRecording()
            is AiAssistantIntent.StopRecording -> stopRecording()
            is AiAssistantIntent.CancelRecording -> cancelRecording()
            is AiAssistantIntent.UpdateInputText -> updateInputText(intent.text)
        }
    }

    /**
     * Updates the input text field in the UI state.
     * 
     * @param text The new text to set in the input field
     */
    private fun updateInputText(text: String) {
        _state.update { it.copy(inputText = text) }
    }

    /**
     * Sends a message to the AI Assistant.
     * Clears the input field, shows loading state, and triggers scroll to bottom.
     * 
     * @param message The message text to send to the AI
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            
            // Add user message and update state
            _state.update { currentState ->
                currentState.copy(
                    inputText = "",
                    isLoading = true,
                    messages = currentState.messages
                )
            }

            // Trigger scroll to bottom event
            _events.send(AiAssistantEvent.ScrollToBottom)

            try {
                // Get document ID from current conversation or use a default
                val documentId = _state.value.currentDocumentId ?: "default"

                // Send message to AI Assistant
                aiAssistantRepository.sendMessage(message, documentId)

                // The response will be handled by the flow collector in startListening()
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Failed to send message: ${e.message}"))
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Handles AI response from the streaming API.
     * Manages message threading for streaming responses and updates UI state accordingly.
     * 
     * @param response The completion response from the AI service
     */
    private fun handleAiResponse(response: CompletionResponse) {
        viewModelScope.launch {
            Log.i("Response"," MessageItem: ${response.end} *${response.sender}* - ${response.timestamp} - *${response.state}* - ${response.content}")

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

    /**
     * Initiates voice recognition recording.
     * Updates the UI state to indicate recording is active and handles any errors.
     */
    private fun startRecording() {
        viewModelScope.launch {
            try {
                voiceRecognitionRepository.startRecognition()
                _state.update { it.copy(isRecording = true) }
            } catch (e: Exception) {
                _events.send(AiAssistantEvent.Error("Error starting voice recording: ${e.message}"))
            }
        }
    }

    /**
     * Stops voice recognition recording and processes the recognized text.
     * Updates the input field with the recognized text and resets recording state.
     */
    private fun stopRecording() {
        viewModelScope.launch {
            try {
                val recognizedText = voiceRecognitionRepository.stopRecognition()
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

    /**
     * Cancels ongoing voice recognition recording.
     * Discards any partial recording text and resets the recording state.
     */
    private fun cancelRecording() {
        viewModelScope.launch {
            try {
                voiceRecognitionRepository.cancelRecognition()
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

    /**
     * Updates the UI state based on voice recognition changes.
     * Handles partial text updates, final text results, and error states.
     * 
     * @param voiceState The current voice recognition state from the repository
     */
    private fun updateVoiceState(voiceState: VoiceRecognitionState) {
        _state.update {
            it.copy(
                partialRecordingText = voiceState.partialText, isRecording = voiceState.isListening
            )
        }

        if (voiceState.error==null && voiceState.text.isNotEmpty()){
            _state.update {
                it.copy(
                    inputText = voiceState.text
                )

            }
        }

        voiceState.error?.let { error ->
            viewModelScope.launch {
                _events.send(AiAssistantEvent.Error(error))
            }
        }
    }

    /**
     * Adds a new message to the conversation state.
     * 
     * @param message The CompletionResponse message to add to the conversation
     */
    private fun addMessage(message: CompletionResponse) {
        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + message
            )
        }
    }

    /**
     * Starts listening to the AI Assistant response stream.
     * Collects incoming responses and processes them through handleAiResponse.
     * This method should be called once when the AI Assistant is initialized.
     */
    fun startListening() {
        viewModelScope.launch {
            aiAssistantRepository.responseStream.collect { response ->
                response?.let { handleAiResponse(it) }
            }
        }
    }


}

/**
 * Extension function to format the timestamp of a CompletionResponse into a readable time string.
 * 
 * @return Formatted time string in "HH:mm aa" format (e.g., "02:30 PM")
 */
fun CompletionResponse.getDate(): String {
    val formatter = SimpleDateFormat("HH:mm aa", Locale.getDefault())
    return formatter.format(Date(this.timestamp))
}