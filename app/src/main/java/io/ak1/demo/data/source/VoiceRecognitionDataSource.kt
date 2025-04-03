package io.ak1.demo.data.source

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import io.ak1.demo.domain.model.VoiceRecognitionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


/**
 * Data source for voice recognition
 */
interface VoiceRecognitionDataSource {
    val state: Flow<VoiceRecognitionState>

    fun startRecognition(languageCode: String = "en")
    fun stopRecognition()
    fun cancelRecognition()
}

/**
 * Implementation of voice recognition data source using Android's SpeechRecognizer
 */
class VoiceRecognitionDataSourceImpl(
    private val context: Context
) : VoiceRecognitionDataSource, RecognitionListener {

    private val _state = MutableStateFlow(VoiceRecognitionState())
    override val state: StateFlow<VoiceRecognitionState> = _state.asStateFlow()

    private val recognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    init {
        recognizer.setRecognitionListener(this)
    }

    override fun startRecognition(languageCode: String) {
        _state.update { VoiceRecognitionState() }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.update {
                it.copy(
                    isListening = false,
                    error = "Recognition is not available on this device"
                )
            }
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }

        recognizer.startListening(intent)
        _state.update { it.copy(isListening = true) }
    }

    override fun stopRecognition() {
        recognizer.stopListening()
        _state.update { it.copy(isListening = false) }
    }

    override fun cancelRecognition() {
        recognizer.cancel()
        _state.update { VoiceRecognitionState() }
    }

    // RecognitionListener implementation
    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { it.copy(error = null) }
    }

    override fun onBeginningOfSpeech() {
        _state.update { it.copy(partialText = "") }
    }

    override fun onRmsChanged(rmsdB: Float) = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _state.update {
            it.copy(
                isListening = false,
                partialText = ""
            )
        }
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }

        _state.update {
            it.copy(
                isListening = false,
                text = "",
                partialText = "",
                error = "Error: $errorMessage"
            )
        }
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)
            ?.let { text ->
                _state.update { it.copy(text = text) }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val partialText = matches[0]
            _state.update { it.copy(partialText = partialText) }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) = Unit
}