package io.ak1.demo.domain.usecase.voice

import io.ak1.demo.domain.model.VoiceRecognitionState
import io.ak1.demo.domain.repository.VoiceRecognitionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for starting voice recognition
 */
class StartVoiceRecognitionUseCase(
    private val voiceRecognitionRepository: VoiceRecognitionRepository
) {
    val voiceState: Flow<VoiceRecognitionState> = voiceRecognitionRepository.voiceState

    suspend fun execute(languageCode: String = "en") {
        voiceRecognitionRepository.startRecognition(languageCode)
    }
}