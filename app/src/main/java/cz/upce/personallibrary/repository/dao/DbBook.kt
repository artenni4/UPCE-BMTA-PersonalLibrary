package cz.upce.personallibrary.repository.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.model.BookRating
import java.time.Year

@Entity(tableName = "books")
data class DbBook(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val author: String,
    val publicationYear: Int,
    val genre: String,
    val personalRating: Float
) {
    fun toBook() = Book(id, title, author, Year.of(publicationYear), genre, BookRating(personalRating))

    companion object {
        fun fromBook(book: Book) = DbBook(
            book.id,
            book.title,
            book.author,
            book.publicationYear.value,
            book.genre,
            book.personalRating.value)
    }
}