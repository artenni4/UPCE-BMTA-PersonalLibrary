package cz.upce.personallibrary.repository.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DbBook::class],
    version = 1,
    exportSchema = false
)
abstract class BooksDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BooksDatabase? = null

        fun getDatabase(context: Context): BooksDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): BooksDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BooksDatabase::class.java,
                "books_database"
            ).build()
        }
    }
}