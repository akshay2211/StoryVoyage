package io.ak1.demo.data.repository

import io.ak1.demo.domain.model.Book

object Books {
    val list = listOf(

        Book(
            id = "one",
            title = "The Canterville Ghost",
            thumbnailUrl = "https://www.infobooks.org/cover/the-canterville-ghost-oscar-wilde.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/v4xvmg7qxg9ffh5t2gp8y/the-canterville-ghost-oscar-wilde.pdf?rlkey=b6j1pg676xy78a09p1a7ryo1g&dl=0",
            author = "Oscar Wilde",
            description = """
            A ghost haunting an English manor encounters a modern American family. 
            Instead of being feared, he is mocked and outwitted. 
            A humorous tale that blends satire with Gothic tropes. 
            It comments on cultural contrasts and the nature of redemption.
            Oscar Wilde was an Irish poet and playwright known for his wit and flamboyance. 
            He is celebrated for works like "The Picture of Dorian Gray" and sharp social commentary.
        """.trimIndent()
        ),

        Book(
            id = "two",
            title = "Alice's Adventures in Wonderland",
            thumbnailUrl = "https://www.infobooks.org/cover/alices-adventures-in-wonderland-lewis-carroll.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/hff0vvuxlk5vrgdzplbox/11.-Alice-s-Adventures-in-Wonderland-Author-Lewis-Carroll.pdf?rlkey=fl4ew65wj97qih0fqhd9v6ejg&dl=0",
            author = "Lewis Carroll",
            description = """
            A whimsical tale of a girl named Alice who falls into a fantastical world. 
            Packed with peculiar creatures, puns, and riddles. 
            It’s a playful exploration of logic, identity, and absurdity. 
            A cornerstone of children’s literature and Victorian fantasy.
            Lewis Carroll was the pen name of Charles Lutwidge Dodgson, a mathematician and writer. 
            He is known for blending logic with literary nonsense.
        """.trimIndent()
        ),
        Book(
            id = "three",
            title = "The Little Prince",
            thumbnailUrl = "https://www.infobooks.org/cover/the-little-prince-antoine-de-saint-exupery.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/om9d8ltsgz7qic6gbtoex/32.-The-Little-Prince-Author-Antoine-de-Saint-Exup-ry.pdf?rlkey=024qxi21hkwvgizt9vkf7xh2o&dl=0",
            author = "Antoine de Saint-Exupéry",
            description = """
            A poetic tale of a young prince who travels from planet to planet. 
            Through simple yet profound observations, it explores love, loss, and imagination. 
            It’s beloved for its philosophical insights and childlike wisdom. 
            A timeless classic that appeals to both children and adults.
            Antoine de Saint-Exupéry was a French writer and pioneering aviator. 
            His works often reflect his love for flying and his philosophical depth.
        """.trimIndent()
        ),
        Book(
            id = "four",
            title = "The Art of War",
            thumbnailUrl = "https://www.infobooks.org/cover/the-art-of-war-sun-tzu.webp",
            pdfUrl = "https://dl.dropboxusercontent.com/scl/fi/brd2s6f56c1i06o8efrjk/The-Art-of-War-Sun-Tzu.pdf?rlkey=wdq11122pamgaxvsuhcisvlzv&dl=0",
            author = "Sun Tzu",
            description = """
            The Art of War by Sun Tzu is a timeless guide to strategy and leadership, emphasizing the importance of planning, deception, and understanding the enemy.
            Its principles extend beyond military conflict, offering valuable insights for success in business, negotiation, and any competitive endeavor.
        """.trimIndent()
        ),
    )
}