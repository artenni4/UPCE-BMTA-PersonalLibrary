package cz.upce.personallibrary.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.upce.personallibrary.R
import cz.upce.personallibrary.model.Book

class BookCatalogueAdapter(private var books: List<Book>) : RecyclerView.Adapter<BookCatalogueAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val authorView: TextView = itemView.findViewById(R.id.textViewAuthor)
        private val yearView: TextView = itemView.findViewById(R.id.textViewPublicationYear)
        private val genreView: TextView = itemView.findViewById(R.id.textViewGenre)
        private val ratingView: TextView = itemView.findViewById(R.id.textViewRating)

        fun bind(book: Book) {
            titleView.text = book.title
            authorView.text = book.author
            yearView.text = book.publicationYear.toString()
            genreView.text = book.genre
            ratingView.text = book.personalRating.toString()
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
        holder.bind(books[position])
    }

    fun updateBooks(newBooks: List<Book>) {
        val diffCallback = BookDiffCallback(books, newBooks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        books = newBooks
        diffResult.dispatchUpdatesTo(this)
    }
}