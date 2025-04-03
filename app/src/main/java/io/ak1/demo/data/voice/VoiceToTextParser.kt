package io.ak1.demo.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import io.ak1.demo.domain.model.VoiceToTextParserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update




class VoiceToTextParser(val context: Context): RecognitionListener {

    val _state = MutableStateFlow(VoiceToTextParserState())
    val state = _state.asStateFlow()
    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)



    fun startRecognition(languageCode : String = "en"){

        _state.update { VoiceToTextParserState() }
        Log.e("Akshay", "startRecognition: ", )
        if (!SpeechRecognizer.isRecognitionAvailable(context)){
            _state.update { it.copy(
                isListening = false,
                error = "Recognition is not available on this device"
            ) }
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)

            // Request more frequent partial results
//            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2500)
//            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10 * 1000)
//            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 7 * 1000)

        }
        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _state.update { it.copy(isListening = true) }

    }

    fun stopRecognition(){
        recognizer.stopListening()
        _state.update { it.copy(isListening = false) }
    }
    override fun onReadyForSpeech(params: Bundle?) {
        _state.update { it.copy(
            error = null
        ) }
    }

    override fun onBeginningOfSpeech()  {
        _state.update { it.copy(
            partialText = ""
        ) }
    }

    override fun onRmsChanged(rmsdB: Float)  = Unit

    override fun onBufferReceived(buffer: ByteArray?) = Unit

    override fun onEndOfSpeech() {
        _state.update { it.copy(
            isListening = false,
            partialText = ""
        ) }
    }

    override fun onError(error: Int) {
        // Handle errors
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
        _state.update { it.copy(
            isListening = false,
            text = "",
            partialText = "",
            error = "Error :$errorMessage"
        ) }
    }

    override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)
            ?.let { text ->
                _state.update { it.copy(
                    text = text
                ) }
            }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        // Process partial speech recognition results
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val partialText = matches[0]
            Log.e("Akshay", "onPartialResults: $partialText", )
            _state.update { it.copy(
                partialText = partialText
            )}
        }

    }

    override fun onEvent(eventType: Int, params: Bundle?)  = Unit

    fun cancelRecognition() {
        recognizer.stopListening()
        _state.update { VoiceToTextParserState() }
    }

}