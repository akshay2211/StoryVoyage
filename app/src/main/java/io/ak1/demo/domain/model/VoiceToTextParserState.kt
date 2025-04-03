package io.ak1.demo.domain.model

data class VoiceToTextParserState(
    val text:String ="",
    val partialText: String = "",
    val isListening:Boolean = false,
    val error:String? = null)