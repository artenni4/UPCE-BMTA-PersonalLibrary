package cz.upce.personallibrary.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import cz.upce.personallibrary.R
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

        if (bookValues != null) {
            updateViewValues(bookValues)
        }

        binding.buttonSave.setOnClickListener {
            if (validateFields()) {
                val editedBook = BookValues(
                    id = bookValues?.id ?: 0,
                    title = binding.editTextTitle.text.toString(),
                    author = binding.editTextAuthor.text.toString(),
                    genre = binding.editTextGenre.text.toString(),
                    publicationYear = Year.parse(binding.editTextPublicationYear.text.toString()),
                    personalRating = BookRating(binding.personalRatingBar.rating))

                val intent = Intent().putExtra(BOOK_VALUES, editedBook)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun updateViewValues(bookValues: BookValues) {
        binding.editTextTitle.setText(bookValues.title)
        binding.editTextAuthor.setText(bookValues.author)
        binding.editTextGenre.setText(bookValues.genre)
        binding.editTextPublicationYear.setText(bookValues.publicationYear.toString())
        binding.personalRatingBar.rating = bookValues.personalRating.value
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (binding.editTextTitle.text.toString().isBlank()) {
            binding.editTextTitle.error = getString(R.string.title_is_required)
            isValid = false
        }
        if (binding.editTextGenre.text.toString().isBlank()) {
            binding.editTextGenre.error = getString(R.string.genre_is_required)
            isValid = false
        }
        if (binding.editTextPublicationYear.text.toString().isBlank()) {
            binding.editTextPublicationYear.error = getString(R.string.publication_year_is_required)
            isValid = false
        }
        if (binding.editTextAuthor.text.toString().isBlank()) {
            binding.editTextAuthor.error = getString(R.string.author_is_required)
            isValid = false
        }

        return isValid
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