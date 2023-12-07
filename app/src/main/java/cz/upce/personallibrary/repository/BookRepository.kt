package cz.upce.personallibrary.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.repository.dao.BookDao
import cz.upce.personallibrary.repository.dao.DbBook

class BookRepository(private val bookDao: BookDao) {
    fun searchBooks(searchQuery: String): LiveData<List<Book>> = bookDao.searchBooks(searchQuery).map {
        dbBookList -> dbBookList.map { it.toBook() }
    }

    suspend fun insert(book: Book) {
        bookDao.insert(DbBook.fromBook(book))
    }

    suspend fun delete(book: Book) {
        bookDao.delete(DbBook.fromBook(book))
    }

    suspend fun update(book: Book) {
        bookDao.update(DbBook.fromBook(book))
    }
}