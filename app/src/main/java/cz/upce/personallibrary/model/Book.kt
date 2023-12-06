package cz.upce.personallibrary.model

import java.time.Year

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val publicationYear: Year,
    val genre: String,
    val personalRating: BookRating)
