package io.ak1.demo.data.repository

import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.data.source.AiAssistantDataSource
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of AiAssistantRepository that handles AI Assistant operations.
 * 
 * This repository serves as a bridge between the domain layer and AI Assistant data source,
 * providing functionality including:
 * - AI Assistant initialization with PDF documents
 * - Real-time streaming response observation
 * - Message sending to AI service
 * - Service lifecycle management and termination
 * 
 * The repository abstracts the complexity of AI service communication and provides
 * a clean interface for ViewModels to interact with AI functionality.
 * 
 * @param aiAssistantDataSource Data source handling direct AI service communication
 */
class AiAssistantRepositoryImpl(
    private val aiAssistantDataSource: AiAssistantDataSource
) : AiAssistantRepository {

    /**
     * Provides a reactive stream of AI Assistant responses.
     * Exposes the data source's response flow for ViewModels to observe.
     */
    override val responseStream: Flow<CompletionResponse?>
        get() = aiAssistantDataSource.responseState

    /**
     * Initializes the AI Assistant with a PDF document.
     * Sets up the AI service connection and prepares for document-aware conversations.
     * 
     * @param pdfDocument The PDF document to associate with the AI Assistant
     * @param dataProvider Data provider for accessing document content
     * @param documentIdentifiers Identifiers for the document in the AI system
     * @param isRefresh Whether this is a refresh of an existing session
     * @return True if initialization was successful, false otherwise
     */
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

    /**
     * Sends a message to the AI Assistant.
     * The response will be available through the responseStream flow.
     * 
     * @param message The message text to send to the AI
     * @param documentId The ID of the document context for the conversation
     */
    override suspend fun sendMessage(message: String, documentId: String) {
        aiAssistantDataSource.emitMessage(message, documentId)
    }

    /**
     * Terminates the AI Assistant session and cleans up resources.
     * Should be called when the AI Assistant is no longer needed.
     */
    override suspend fun terminate() {
        aiAssistantDataSource.terminate()
    }
}