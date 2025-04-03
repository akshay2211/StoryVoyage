package io.ak1.demo.presentation.viewer

import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.nutrient.data.models.DocumentIdentifiers


/**
 * Represents the UI state for the PDF viewer
 */
data class PdfViewerState(
    val documentId: String = "",
    val documentTitle: String = "",
    val isToolbarVisible: Boolean = true,
    val isLoading: Boolean = true,
    val isAiAssistantInitialized: Boolean = false,
    val isAiAssistantDrawerOpen: Boolean = false,
    val error: String? = null
)

/**
 * Sealed interface representing user intents for the PDF viewer
 */
sealed interface PdfViewerIntent {
    object NavigateBack : PdfViewerIntent
    data class SetToolbarVisibility(val visible: Boolean) : PdfViewerIntent
    data class InitializeAiAssistant(
        val pdfDocument: PdfDocument,
        val dataProvider: DataProvider,
        val documentIdentifiers: DocumentIdentifiers
    ) : PdfViewerIntent
}

/**
 * Sealed interface representing one-time events for the PDF viewer UI
 */
sealed interface PdfViewerEvent {
    data class Error(val message: String) : PdfViewerEvent
    object NavigateBack : PdfViewerEvent
}