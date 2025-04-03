package io.ak1.demo.domain.usecase

import io.ak1.demo.domain.repository.AiAssistantRepository

/**
 * Use case to terminate the AI Assistant
 */
class TerminateAiAssistantUseCase(
    private val aiAssistantRepository: AiAssistantRepository
) {
    suspend fun execute() {
        aiAssistantRepository.terminate()
    }
}