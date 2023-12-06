package cz.upce.personallibrary.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.upce.personallibrary.databinding.ActivityBookCatalogueBinding
import cz.upce.personallibrary.repository.BookRepository
import cz.upce.personallibrary.repository.dao.BooksDatabase
import cz.upce.personallibrary.viewmodel.BookCatalogueViewModel

class BookCatalogueActivity : AppCompatActivity() {
    private lateinit var viewModel: BookCatalogueViewModel
    private lateinit var binding: ActivityBookCatalogueBinding

    private lateinit var adapter: BookCatalogueAdapter

    private lateinit var addBookResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editBookResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookCatalogueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initResultLaunchers()
        initRecyclerViewAdapter()

        initAddBookAction()
        setDeleteOnSwipe()
        setEditOnTap()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, BookCatalogueViewModel.Factory(BookRepository(
            BooksDatabase.getDatabase(this).bookDao()
        )))[BookCatalogueViewModel::class.java]
    }

    private fun initResultLaunchers() {
        addBookResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.parcelable<AddEditBookActivity.BookValues>(AddEditBookActivity.BOOK_VALUES)?.let {
                    viewModel.addBook(it.toBook())
                }
            }
        }

        editBookResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.parcelable<AddEditBookActivity.BookValues>(AddEditBookActivity.BOOK_VALUES)?.let {
                    viewModel.editBook(it.toBook())
                }
            }
        }
    }

    private fun initRecyclerViewAdapter() {
        adapter = BookCatalogueAdapter(viewModel.allBooks.value ?: listOf())
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.booksRecyclerView.adapter = adapter

        viewModel.allBooks.observe(this) { books ->
            adapter.updateBooks(books)
        }
    }

    private fun setDeleteOnSwipe() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val book = adapter.values[position]
                viewModel.deleteBook(book)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.booksRecyclerView)
    }

    private fun setEditOnTap() {
        adapter.setOnItemClickListener {
            val intent = Intent(this, AddEditBookActivity::class.java)
                .putExtra(AddEditBookActivity.BOOK_VALUES, AddEditBookActivity.BookValues.fromBook(it))

            addBookResultLauncher.launch(intent)
        }
    }

    private fun initAddBookAction() {
        binding.fabAddBook.setOnClickListener {
            addBookResultLauncher.launch(Intent(this, AddEditBookActivity::class.java))
        }
    }
}