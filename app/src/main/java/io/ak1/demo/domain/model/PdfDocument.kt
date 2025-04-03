package io.ak1.demo.domain.model

import androidx.compose.runtime.Immutable
import io.ak1.demo.R

data class PdfDocument(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val pdfUrl: String,
    val author: String
)

@Immutable
data class Message(
    val author: String,
    val content: String,
    val timestamp: String,
    val image: Int? = null,
    val authorImage: Int = if (author == "me") R.drawable.ic_launcher_background else R.drawable.ic_launcher_foreground,
)
