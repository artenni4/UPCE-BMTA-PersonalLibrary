package cz.upce.personallibrary.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import cz.upce.personallibrary.databinding.ActivityAddEditBookBinding
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.model.BookRating
import kotlinx.parcelize.Parcelize
import java.time.Year

class AddEditBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookValues = intent.parcelable<BookValues>(BOOK_VALUES)

        binding.buttonSave.setOnClickListener {
            val editedBook = BookValues(
                id = bookValues?.id ?: 0,
                title = binding.editTextTitle.text.toString(),
                author = binding.editTextAuthor.text.toString(),
                genre = binding.editTextGenre.text.toString(),
                publicationYear = Year.parse(binding.editTextPublicationYear.text.toString()),
                personalRating = BookRating(binding.personalRatingBar.numStars))

            val intent = Intent().putExtra(BOOK_VALUES, editedBook)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    @Parcelize
    data class BookValues(
        val id: Int,
        val title: String,
        val author: String,
        val publicationYear: Year,
        val genre: String,
        val personalRating: BookRating) : Parcelable {
            fun toBook() = Book(id, title, author, publicationYear, genre, personalRating)

            companion object {
                fun fromBook(book: Book) = BookValues(book.id, book.title, book.author, book.publicationYear, book.genre, book.personalRating)
            }
        }

    companion object {
        const val BOOK_VALUES = "BOOK_VALUES"
    }
}