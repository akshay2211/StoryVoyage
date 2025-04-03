package io.ak1.demo.data.repository

import io.ak1.demo.domain.model.Book

object Books {
    val list = listOf(
        Book(
            id = "one",
            title = "The Little Prince",
            thumbnailUrl = "https://www.infobooks.org/cover/the-little-prince-antoine-de-saint-exupery.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/om9d8ltsgz7qic6gbtoex/32.-The-Little-Prince-Author-Antoine-de-Saint-Exup-ry.pdf?rlkey=024qxi21hkwvgizt9vkf7xh2o&dl=0",
            author = "Antoine de Saint-Exupéry"
        ),
        Book(
            id = "two",
            title = "Alice's Adventures in Wonderland",
            thumbnailUrl = "https://www.infobooks.org/cover/alices-adventures-in-wonderland-lewis-carroll.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/hff0vvuxlk5vrgdzplbox/11.-Alice-s-Adventures-in-Wonderland-Author-Lewis-Carroll.pdf?rlkey=fl4ew65wj97qih0fqhd9v6ejg&dl=0",
            author = "Lewis Carroll"
        ),
        Book(
            id = "three",
            title = "The Canterville Ghost",
            thumbnailUrl = "https://www.infobooks.org/cover/the-canterville-ghost-oscar-wilde.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/v4xvmg7qxg9ffh5t2gp8y/the-canterville-ghost-oscar-wilde.pdf?rlkey=b6j1pg676xy78a09p1a7ryo1g&dl=0",
            author = "Oscar Wilde"
        )
    )
}