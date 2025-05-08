package io.ak1.demo.domain.model

data class Book(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val pdfUrl: String,
    val author: String,
    val description: String
)

