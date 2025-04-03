package io.ak1.demo.domain.usecase.voice

import io.ak1.demo.domain.repository.VoiceRecognitionRepository


/**
 * Use case for canceling voice recognition
 */
class CancelVoiceRecognitionUseCase(
    private val voiceRecognitionRepository: VoiceRecognitionRepository
) {
    suspend fun execute() {
        voiceRecognitionRepository.cancelRecognition()
    }
}