package io.ak1.demo.domain.model


/**
 * Voice recognition state
 */
data class VoiceRecognitionState(
    val text: String = "",
    val partialText: String = "",
    val isListening: Boolean = false,
    val error: String? = null
)