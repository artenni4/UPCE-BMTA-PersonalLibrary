package cz.upce.personallibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.repository.BookRepository

class BookCatalogueViewModel(repository: BookRepository) : ViewModel() {

    class Factory(private val repository: BookRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookCatalogueViewModel::class.java)) {
                return BookCatalogueViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown view-model class")
        }
    }

    val allBooks: LiveData<List<Book>> = repository.allBooks

    fun addBook(book: Book) {
        TODO()
    }
}