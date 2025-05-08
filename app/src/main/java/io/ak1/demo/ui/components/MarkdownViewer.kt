package io.ak1.demo.ui.components
import android.content.Context
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream

@Composable
fun MarkdownViewer(
    modifier: Modifier = Modifier,
    markdownSource: MarkdownSource
) {
    val context = LocalContext.current
    var markdownContent by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Create Markwon instance with plugins
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(StrikethroughPlugin.create())
            .build()
    }

    // Load markdown content based on source
    LaunchedEffect(markdownSource) {
        markdownContent = when (markdownSource) {
            is MarkdownSource.FromFile -> readMarkdownFromFile(markdownSource.file)
            is MarkdownSource.FromAsset -> readMarkdownFromAsset(context, markdownSource.assetName)
            is MarkdownSource.FromString -> markdownSource.content
            is MarkdownSource.FromInputStream -> readMarkdownFromInputStream(markdownSource.inputStream)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val color = MaterialTheme.colorScheme.onBackground.toArgb()
        AndroidView(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            factory = { ctx ->
                TextView(ctx).apply {
                    // Apply some basic styling to match Material theme
                    setTextColor(color)
                    textSize = 16f
                }
            },
            update = { textView ->
                // Apply the markdown to the TextView
                markwon.setMarkdown(textView, markdownContent)
            }
        )
    }
}

// Define different sources for Markdown content
sealed class MarkdownSource {
    data class FromFile(val file: File) : MarkdownSource()
    data class FromAsset(val assetName: String) : MarkdownSource()
    data class FromString(val content: String) : MarkdownSource()
    data class FromInputStream(val inputStream: InputStream) : MarkdownSource()
}

// Helper functions to read Markdown content from different sources
private suspend fun readMarkdownFromFile(file: File): String = withContext(Dispatchers.IO) {
    try {
        BufferedReader(FileReader(file)).use { reader ->
            reader.readText()
        }
    } catch (e: Exception) {
        "Error reading Markdown file: ${e.message}"
    }
}

private suspend fun readMarkdownFromAsset(context: Context, assetName: String): String = withContext(Dispatchers.IO) {
    try {
        context.assets.open(assetName).bufferedReader().use { reader ->
            reader.readText()
        }
    } catch (e: Exception) {
        "Error reading Markdown from assets: ${e.message}"
    }
}

private suspend fun readMarkdownFromInputStream(inputStream: InputStream): String = withContext(Dispatchers.IO) {
    try {
        inputStream.bufferedReader().use { reader ->
            reader.readText()
        }
    } catch (e: Exception) {
        "Error reading Markdown from input stream: ${e.message}"
    }
}