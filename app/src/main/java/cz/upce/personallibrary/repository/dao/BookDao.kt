package cz.upce.personallibrary.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Insert
    suspend fun insert(book: DbBook)

    @Update
    suspend fun update(book: DbBook)

    @Delete
    suspend fun delete(book: DbBook)

    @Query("SELECT * FROM books WHERE title LIKE :searchQuery OR author LIKE :searchQuery")
    fun searchBooks(searchQuery: String): LiveData<List<DbBook>>
}

