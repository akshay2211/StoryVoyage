package io.ak1.demo.domain.usecase

import io.ak1.demo.domain.repository.AiAssistantRepository
import io.nutrient.data.models.CompletionResponse
import kotlinx.coroutines.flow.Flow

/**
 * Use case for sending messages to the AI Assistant and observing responses
 */
class SendMessageUseCase(
    private val aiAssistantRepository: AiAssistantRepository
) {
    suspend fun execute(message: String, documentId: String) {
        aiAssistantRepository.sendMessage(message, documentId)
    }

    fun getResponseStream(): Flow<CompletionResponse?> {
        return aiAssistantRepository.responseStream
    }
}
