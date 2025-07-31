package io.ak1.demo.presentation.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for managing PDF viewer functionality and AI Assistant integration.
 * 
 * This ViewModel implements the MVI (Model-View-Intent) pattern for handling:
 * - PDF document viewing and navigation
 * - AI Assistant initialization and state management
 * - Toolbar visibility control
 * - Error handling for document operations
 * 
 * The PDF viewer integrates with the Nutrient SDK for document rendering
 * and provides seamless AI assistance for document interaction.
 * 
 * @param aiAssistantRepository Repository for AI Assistant operations and initialization
 */
class PdfViewerViewModel(
    private val aiAssistantRepository: AiAssistantRepository
) : ViewModel() {

    /** Mutable state flow for managing PDF viewer UI state */
    private val _state = MutableStateFlow(PdfViewerState())
    
    /** Exposed read-only state flow for UI observation */
    val state: StateFlow<PdfViewerState> = _state.asStateFlow()

    /** Channel for one-time UI events */
    private val _events = Channel<PdfViewerEvent>()
    
    /** Exposed flow for UI events like navigation and errors */
    val events = _events.receiveAsFlow()

    /**
     * Processes user intents using the MVI pattern.
     * Routes different types of user actions to appropriate handler methods.
     * 
     * @param intent The user intent to process
     */
    fun processIntent(intent: PdfViewerIntent) {
        when (intent) {
            is PdfViewerIntent.NavigateBack -> navigateBack()
            is PdfViewerIntent.SetToolbarVisibility -> setToolbarVisibility(intent.visible)
            is PdfViewerIntent.InitializeAiAssistant -> initializeAiAssistant(
                intent.pdfDocument,
                intent.dataProvider,
                intent.documentIdentifiers
            )
        }
    }

    /**
     * Triggers navigation back to the previous screen.
     */
    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PdfViewerEvent.NavigateBack)
        }
    }


    /**
     * Updates the toolbar visibility state.
     * 
     * @param visible True to show the toolbar, false to hide it
     */
    private fun setToolbarVisibility(visible: Boolean) {
        _state.update { it.copy(isToolbarVisible = visible) }
    }

    /**
     * Initializes the AI Assistant with the provided PDF document.
     * Sets up the AI service connection and prepares for document-aware conversations.
     * 
     * @param pdfDocument The PDF document to associate with the AI Assistant
     * @param dataProvider Data provider for accessing document content
     * @param documentIdentifiers Identifiers for the document in the AI system
     */
    private fun initializeAiAssistant(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val success = aiAssistantRepository.initialize(
                    pdfDocument,
                    dataProvider,
                    documentIdentifiers,
                    true
                )

                _state.update {
                    it.copy(
                        isAiAssistantInitialized = success,
                        isLoading = false
                    )
                }

                if (!success) {
                    _events.send(PdfViewerEvent.Error("Failed to initialize AI Assistant"))
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _events.send(PdfViewerEvent.Error("Error initializing AI Assistant: ${e.message}"))
            }
        }
    }
}