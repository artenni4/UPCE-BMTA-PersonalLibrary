package cz.upce.personallibrary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.repository.dao.BookDao
import cz.upce.personallibrary.repository.dao.DbBook

class BookRepository(private val bookDao: BookDao) {
    val allBooks: LiveData<List<Book>> = Transformations.map(bookDao.getAllBooks()) {
        dbBookList -> dbBookList.map { it.toBook() }
    }

    suspend fun insert(book: Book) {
        bookDao.insert(DbBook.fromBook(book))
    }
}