package cz.upce.personallibrary.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cz.upce.personallibrary.databinding.ActivityBookCatalogueBinding
import cz.upce.personallibrary.repository.BookRepository
import cz.upce.personallibrary.repository.dao.BooksDatabase
import cz.upce.personallibrary.viewmodel.BookCatalogueViewModel

class BookCatalogueActivity : AppCompatActivity() {
    private lateinit var viewModel: BookCatalogueViewModel
    private lateinit var binding : ActivityBookCatalogueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookCatalogueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, BookCatalogueViewModel.Factory(BookRepository(
            BooksDatabase.getDatabase(this).bookDao()
        )))[BookCatalogueViewModel::class.java]
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = BookCatalogueAdapter(listOf())
        binding.booksRecyclerView.adapter = adapter

        viewModel.allBooks.observe(this) { books ->
            adapter.updateBooks(books)
        }
    }
}