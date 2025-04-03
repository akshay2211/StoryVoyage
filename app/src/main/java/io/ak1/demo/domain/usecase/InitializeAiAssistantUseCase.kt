package io.ak1.demo.domain.usecase

import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.nutrient.data.models.DocumentIdentifiers


/**
 * Use case to initialize the AI Assistant
 */
class InitializeAiAssistantUseCase(
    private val aiAssistantRepository: AiAssistantRepository
) {

    suspend fun execute(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers,
        isRefresh: Boolean = false
    ): Boolean {
        return aiAssistantRepository.initialize(
            pdfDocument, dataProvider, documentIdentifiers, isRefresh
        )
    }
}