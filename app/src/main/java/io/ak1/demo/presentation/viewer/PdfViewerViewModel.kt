package io.ak1.demo.presentation.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.providers.DataProvider
import io.ak1.demo.domain.usecase.InitializeAiAssistantUseCase
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PdfViewerViewModel(
    private val initializeAiAssistantUseCase: InitializeAiAssistantUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PdfViewerState())
    val state: StateFlow<PdfViewerState> = _state.asStateFlow()

    private val _events = Channel<PdfViewerEvent>()
    val events = _events.receiveAsFlow()

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

    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(PdfViewerEvent.NavigateBack)
        }
    }


    private fun setToolbarVisibility(visible: Boolean) {
        _state.update { it.copy(isToolbarVisible = visible) }
    }

    private fun initializeAiAssistant(
        pdfDocument: PdfDocument,
        dataProvider: DataProvider,
        documentIdentifiers: DocumentIdentifiers
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val success = initializeAiAssistantUseCase.execute(
                    pdfDocument,
                    dataProvider,
                    documentIdentifiers,
                    false,  // Not a refresh
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