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

val titleRegex = Regex("[^a-zA-Z0-9]")
/**
 * Data source for AI Assistant operations
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
 * Implementation of AI Assistant data source using an external service
 */
class AiAssistantDataSourceImpl(
    private val context: Context
) : AiAssistantDataSource {

    var aiAssistant: AiAssistant? = null

    override val responseState: Flow<CompletionResponse?>
        get() = aiAssistant?.responseState ?: emptyFlow()


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

    override suspend fun emitMessage(message: String, documentId: String) {
        val id = aiAssistant?.identifiers?.permanentId ?: ""
        aiAssistant?.emitMessage(message, id)
    }


    override suspend fun terminate() {
        aiAssistant?.terminate()
    }
}
