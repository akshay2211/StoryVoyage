package io.ak1.demo.domain.repository

import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.flow.Flow


/**
 * Repository interface for AI Assistant operations
 */
interface AiAssistantRepository {
    /**
     * Stream of responses from the AI assistant
     */
    val responseStream: Flow<CompletionResponse?>

    /**
     * Initialize with document provider and identifiers
     */
    suspend fun initialize(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers,
        isRefresh: Boolean = false
    ): Boolean

    /**
     * Send a message to the AI assistant
     */
    suspend fun sendMessage(message: String, documentId: String)

    /**
     * Terminate the AI assistant
     */
    suspend fun terminate()
}
