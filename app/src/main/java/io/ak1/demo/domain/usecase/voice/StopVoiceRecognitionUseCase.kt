package io.ak1.demo.domain.usecase.voice

import io.ak1.demo.domain.repository.VoiceRecognitionRepository

/**
 * Use case for stopping voice recognition and returning the recognized text
 */
class StopVoiceRecognitionUseCase(
    private val voiceRecognitionRepository: VoiceRecognitionRepository
) {
    suspend fun execute(): String {
        return voiceRecognitionRepository.stopRecognition()
    }
}