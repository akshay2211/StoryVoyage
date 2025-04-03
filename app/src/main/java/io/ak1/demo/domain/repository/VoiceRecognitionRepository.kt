package io.ak1.demo.domain.repository

import io.ak1.demo.domain.model.VoiceRecognitionState
import kotlinx.coroutines.flow.Flow


/**
 * Repository interface for voice recognition operations
 */
interface VoiceRecognitionRepository {
    /**
     * Current state of voice recognition
     */
    val voiceState: Flow<VoiceRecognitionState>

    /**
     * Start voice recognition
     */
    suspend fun startRecognition(languageCode: String = "en")

    /**
     * Stop voice recognition and return recognized text
     */
    suspend fun stopRecognition(): String

    /**
     * Cancel voice recognition
     */
    suspend fun cancelRecognition()
}