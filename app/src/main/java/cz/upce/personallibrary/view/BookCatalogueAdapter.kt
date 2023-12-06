package cz.upce.personallibrary.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.upce.personallibrary.R
import cz.upce.personallibrary.model.Book

class BookCatalogueAdapter(private var books: List<Book>) : RecyclerView.Adapter<BookCatalogueAdapter.BookViewHolder>() {

    private var onItemClicked: ((Book) -> Unit)? = null

    fun setOnItemClickListener(listener: (Book) -> Unit) {
        this.onItemClicked = listener
    }

    val values: List<Book>
        get() = this.books;

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val authorView: TextView = itemView.findViewById(R.id.textViewAuthor)
        private val yearView: TextView = itemView.findViewById(R.id.textViewPublicationYear)
        private val genreView: TextView = itemView.findViewById(R.id.textViewGenre)
        private val ratingView: RatingBar = itemView.findViewById(R.id.bookRatingBar)

        fun bind(book: Book, onItemClickListener: ((Book) -> Unit)?) {
            titleView.text = book.title
            authorView.text = book.author
            yearView.text = book.publicationYear.toString()
            genreView.text = book.genre
            ratingView.numStars = book.personalRating.value

            onItemClickListener?.let {
                itemView.setOnClickListener {
                    it(book)
                }
            }
        }
    }

    class BookDiffCallback(private val oldList: List<Book>, private val newList: List<Book>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_catalogue_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position], onItemClicked)
    }

    fun updateBooks(newBooks: List<Book>) {
        val diffCallback = BookDiffCallback(books, newBooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        books = newBooks
        diffResult.dispatchUpdatesTo(this)
    }
}