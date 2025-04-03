package io.ak1.demo.data.repository

import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.data.source.AiAssistantDataSource
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.flow.Flow

class AiAssistantRepositoryImpl(
    private val aiAssistantDataSource: AiAssistantDataSource
) : AiAssistantRepository {

    override val responseStream: Flow<CompletionResponse?>
        get() = aiAssistantDataSource.responseState

    override suspend fun initialize(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers,
        isRefresh: Boolean
    ): Boolean {
        return aiAssistantDataSource.initialize(
            pdfDocument,
            dataProvider,
            documentIdentifiers,
            isRefresh
        )
    }

    override suspend fun sendMessage(message: String, documentId: String) {
        aiAssistantDataSource.emitMessage(message, documentId)
    }



    override suspend fun terminate() {
        aiAssistantDataSource.terminate()
    }
}