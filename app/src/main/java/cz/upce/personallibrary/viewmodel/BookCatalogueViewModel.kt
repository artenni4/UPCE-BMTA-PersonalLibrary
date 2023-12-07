package cz.upce.personallibrary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.repository.BookRepository
import kotlinx.coroutines.launch

class BookCatalogueViewModel(private val repository: BookRepository) : ViewModel() {
    class Factory(private val repository: BookRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookCatalogueViewModel::class.java)) {
                return BookCatalogueViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown view-model class")
        }
    }

    private val currentQuery = MutableLiveData<String>()
    val books: LiveData<List<Book>> = currentQuery.switchMap { query ->
        repository.searchBooks("%$query%")
    }

    private var deletedBook: Book? = null

    fun setSearch(searchQuery: String) {
        currentQuery.value = searchQuery;
    }

    fun addBook(book: Book) = viewModelScope.launch {
        repository.insert(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch {
        repository.delete(book)
        deletedBook = book
    }

    fun editBook(book: Book) = viewModelScope.launch {
        repository.update(book)
    }

    fun restoreBook() = viewModelScope.launch {
        deletedBook?.let { book ->
            repository.insert(book)
            deletedBook = null
        }
    }
}