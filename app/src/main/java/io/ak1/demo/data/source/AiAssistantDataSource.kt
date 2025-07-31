package io.ak1.demo.data.source

import android.content.Context
import android.util.Log
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.data.util.JwtGenerator
import io.ak1.demo.ipAddress
import io.nutrient.data.models.AiAssistantConfiguration
import io.nutrient.data.models.CompletionResponse
import io.nutrient.data.models.DocumentIdentifiers
import io.nutrient.domain.ai.AiAssistant
import io.nutrient.domain.ai.standaloneAiAssistant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Regular expression for sanitizing document titles for session naming */
val titleRegex = Regex("[^a-zA-Z0-9]")

/**
 * Data source interface for AI Assistant operations.
 * 
 * Defines the contract for AI Assistant functionality including:
 * - Service initialization with PDF documents
 * - Real-time response streaming
 * - Message emission to AI service
 * - Service lifecycle management
 */
interface AiAssistantDataSource {
    val responseState: Flow<CompletionResponse?>

    suspend fun initialize(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers,
        isRefresh: Boolean = false
    ): Boolean

    suspend fun emitMessage(message: String, documentId: String)
    suspend fun terminate()
}

/**
 * Implementation of AI Assistant data source using Nutrient's standalone AI service.
 * 
 * This data source handles direct communication with the AI Assistant service including:
 * - JWT-based authentication with configurable claims
 * - Document-aware AI Assistant initialization
 * - Real-time streaming response handling
 * - Session management with sanitized document titles
 * - Service termination and cleanup
 * 
 * The implementation uses the Nutrient SDK's standalone AI Assistant functionality
 * and connects to an external AI service running on a configurable IP address.
 * 
 * @param context Android context for accessing application resources and services
 */
class AiAssistantDataSourceImpl(
    private val context: Context
) : AiAssistantDataSource {

    /** Instance of the AI Assistant service, nullable until initialized */
    var aiAssistant: AiAssistant? = null

    /**
     * Provides a reactive stream of AI Assistant responses.
     * Returns empty flow if AI Assistant is not initialized.
     */
    override val responseState: Flow<CompletionResponse?>
        get() = aiAssistant?.responseState ?: emptyFlow()


    /**
     * Initializes the AI Assistant with a PDF document and establishes service connection.
     * 
     * Creates a JWT token with document and session information, configures the AI service,
     * and associates it with the PDF document for context-aware conversations.
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

        return try {
            val session = pdfDocument.title?.replace(titleRegex, "") ?: "default-session"
                val aiAssistantConfiguration = AiAssistantConfiguration(
                    "http://$ipAddress:4000", JwtGenerator.generateJwtToken(
                        context, claims = mapOf(
                            "document_ids" to listOf(documentIdentifiers.permanentId),
                            "session_ids" to listOf(session),
                            "request_limit" to mapOf("requests" to 30, "time_period_s" to 1000 * 60)
                        )
                    ), session
                )
                aiAssistant = standaloneAiAssistant(context, aiAssistantConfiguration)
                pdfDocument.setAiAssistant(aiAssistant!!)
            aiAssistant?.initialize(dataProvider, documentIdentifiers, isRefresh)
            true
        } catch (e: Exception) {
            Log.e("AiAssistant", "Failed to initialize AI Assistant with document provider", e)
            false
        }
    }

    /**
     * Sends a message to the AI Assistant service.
     * Uses the AI Assistant's permanent document ID for message routing.
     * 
     * @param message The message text to send to the AI
     * @param documentId The document ID context (currently unused, using AI Assistant's ID)
     */
    override suspend fun emitMessage(message: String, documentId: String) {
        val id = aiAssistant?.identifiers?.permanentId ?: ""
        aiAssistant?.emitMessage(message, id)
    }


    /**
     * Terminates the AI Assistant service and cleans up resources.
     * Should be called when the AI Assistant is no longer needed.
     */
    override suspend fun terminate() {
        aiAssistant?.terminate()
    }
}
