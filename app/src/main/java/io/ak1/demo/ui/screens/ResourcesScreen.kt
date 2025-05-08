@file:OptIn(ExperimentalMaterial3Api::class)

package io.ak1.demo.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ak1.demo.ui.components.MarkdownSource
import io.ak1.demo.ui.components.MarkdownViewer

@Composable
fun ResourcesScreen(navTo: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Resources and Licenses") }, navigationIcon = {
            IconButton({
                navTo.invoke()
            }) { Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "back") }
        })
    }) { padding ->
        MarkdownViewer(
            modifier = Modifier.padding(padding),
            markdownSource = MarkdownSource.FromAsset("RESOURCES_AND_LICENSES.md")
        )
    }

}