package io.ak1.demo.data.repository

import io.ak1.demo.data.source.VoiceRecognitionDataSource
import io.ak1.demo.domain.model.VoiceRecognitionState
import io.ak1.demo.domain.repository.VoiceRecognitionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VoiceRecognitionRepositoryImpl(
    private val voiceRecognitionDataSource: VoiceRecognitionDataSource
) : VoiceRecognitionRepository {

    override val voiceState: Flow<VoiceRecognitionState> = voiceRecognitionDataSource.state

    override suspend fun startRecognition(languageCode: String) {
        voiceRecognitionDataSource.startRecognition(languageCode)
    }

    override suspend fun stopRecognition(): String {
        voiceRecognitionDataSource.stopRecognition()

        // Wait briefly for recognition to complete and return the recognized text
        delay(300)
        return voiceRecognitionDataSource.state.first().text
    }

    override suspend fun cancelRecognition() {
        voiceRecognitionDataSource.cancelRecognition()
    }
}