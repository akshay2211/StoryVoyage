package io.ak1.demo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import io.ak1.demo.ui.screens.AiAssistantScreen


/**
 * Main AI Assistant View component
 */
@Composable
fun AiAssistantView(
    viewModel: PdfViewerViewModel
) {
//    val state by viewModel.state.collectAsState()

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxWidth()
    ) {

        // Load the full AI assistant screen
        AiAssistantScreen(viewModel)
    }
}