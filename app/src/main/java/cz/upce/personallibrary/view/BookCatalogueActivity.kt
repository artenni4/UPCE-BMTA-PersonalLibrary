package cz.upce.personallibrary.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import cz.upce.personallibrary.R
import cz.upce.personallibrary.databinding.ActivityBookCatalogueBinding
import cz.upce.personallibrary.model.Book
import cz.upce.personallibrary.model.BookRating
import cz.upce.personallibrary.repository.BookRepository
import cz.upce.personallibrary.repository.dao.BooksDatabase
import cz.upce.personallibrary.viewmodel.BookCatalogueViewModel
import java.time.Year

class BookCatalogueActivity : AppCompatActivity() {
    private lateinit var viewModel: BookCatalogueViewModel
    private lateinit var binding: ActivityBookCatalogueBinding

    private lateinit var adapter: BookCatalogueAdapter

    private lateinit var addBookResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editBookResultLauncher: ActivityResultLauncher<Intent>

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookCatalogueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initResultLaunchers()
        initRecyclerView()
        initTopBar()

        initAddBookAction()
        setDeleteOnSwipe()
        setEditOnTap()
    }

    private fun initTopBar() {
        binding.topAppBar.menu.findItem(R.id.action_search).let { menuItem ->
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.setSearch(searchView.query.toString())
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }
                    searchHandler.postDelayed(Runnable {
                        viewModel.setSearch(newText ?: "")
                    }.also { searchRunnable = it }, 400)

                    return true
                }
            })

            menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    viewModel.setSearch("")
                    return true
                }
            })
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.testing_data_action) {
                viewModel.addBook(Book(0, "Book1", "Author1", Year.of(2000), "Genre1", BookRating(4.0f)))
                viewModel.addBook(Book(0, "Book1 Part 2", "Author3", Year.of(2004), "Genre1", BookRating(5.0f)))
                viewModel.addBook(Book(0, "Book2", "Author1", Year.of(2001), "Genre2", BookRating(1.0f)))
                viewModel.addBook(Book(0, "Book3", "Author2", Year.of(2002), "Genre2", BookRating(5.0f)))
                viewModel.addBook(Book(0, "Book4", "Author3", Year.of(2004), "Genre3", BookRating(3.0f)))
                return@setOnMenuItemClickListener true
            }

            return@setOnMenuItemClickListener false
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, BookCatalogueViewModel.Factory(BookRepository(
            BooksDatabase.getDatabase(this).bookDao()
        )))[BookCatalogueViewModel::class.java]
    }

    private fun initResultLaunchers() {
        addBookResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }

            result.data?.parcelable<AddEditBookActivity.BookValues>(AddEditBookActivity.BOOK_VALUES)?.let { bookValues ->
                val book = bookValues.toBook()
                viewModel.addBook(book)
            }
        }

        editBookResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    data.parcelable<AddEditBookActivity.BookValues>(AddEditBookActivity.BOOK_VALUES)?.let { bookValues ->
                        viewModel.editBook(bookValues.toBook())
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        adapter = BookCatalogueAdapter(viewModel.books.value ?: listOf())
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.booksRecyclerView.adapter = adapter
        binding.booksRecyclerView.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin)))

        viewModel.books.observe(this) { books ->
            adapter.updateBooks(books)
        }
        viewModel.setSearch("")
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

                showUndoSnackbar()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.booksRecyclerView)
    }

    fun showUndoSnackbar() {
        Snackbar.make(binding.booksRecyclerView, R.string.book_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.restoreBook()
            }.show()
    }

    private fun setEditOnTap() {
        adapter.setOnItemClickListener { book ->
            val intent = Intent(this, AddEditBookActivity::class.java)
                .putExtra(AddEditBookActivity.BOOK_VALUES, AddEditBookActivity.BookValues.fromBook(book))

            editBookResultLauncher.launch(intent)
        }
    }

    private fun initAddBookAction() {
        binding.fabAddBook.setOnClickListener {
            addBookResultLauncher.launch(Intent(this, AddEditBookActivity::class.java))
        }
    }
}