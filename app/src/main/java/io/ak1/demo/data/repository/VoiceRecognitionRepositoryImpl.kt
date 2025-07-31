package io.ak1.demo.data.repository

import io.ak1.demo.data.source.VoiceRecognitionDataSource
import io.ak1.demo.domain.model.VoiceRecognitionState
import io.ak1.demo.domain.repository.VoiceRecognitionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Implementation of VoiceRecognitionRepository that handles voice recognition operations.
 * 
 * This repository manages voice recognition functionality including:
 * - Voice recognition session lifecycle management
 * - Real-time voice recognition state observation
 * - Text extraction from completed recognition sessions
 * - Error handling and session cancellation
 * 
 * The repository provides a clean interface between ViewModels and the voice recognition
 * data source, handling timing and state management complexities.
 * 
 * @param voiceRecognitionDataSource Data source handling direct voice recognition operations
 */
class VoiceRecognitionRepositoryImpl(
    private val voiceRecognitionDataSource: VoiceRecognitionDataSource
) : VoiceRecognitionRepository {

    /**
     * Provides a reactive stream of voice recognition state changes.
     * Exposes real-time updates including partial text, completion status, and errors.
     */
    override val voiceState: Flow<VoiceRecognitionState> = voiceRecognitionDataSource.state

    /**
     * Starts a voice recognition session with the specified language.
     * 
     * @param languageCode The language code for recognition (e.g., "en-US")
     */
    override suspend fun startRecognition(languageCode: String) {
        voiceRecognitionDataSource.startRecognition(languageCode)
    }

    /**
     * Stops the current voice recognition session and returns the recognized text.
     * Includes a brief delay to allow recognition processing to complete.
     * 
     * @return The final recognized text from the session
     */
    override suspend fun stopRecognition(): String {
        voiceRecognitionDataSource.stopRecognition()

        // Wait briefly for recognition to complete and return the recognized text
        delay(300)
        return voiceRecognitionDataSource.state.first().text
    }

    /**
     * Cancels the current voice recognition session without returning results.
     * Cleans up resources and resets the recognition state.
     */
    override suspend fun cancelRecognition() {
        voiceRecognitionDataSource.cancelRecognition()
    }
}