package cz.upce.personallibrary.model

import java.io.Serializable

data class BookRating(val value: Int) : Comparable<BookRating>, Serializable {
    init {
        if (value < 1 || value > 5) {
            throw IllegalArgumentException("Rating out of range")
        }
    }

    override fun toString() = "$value"

    override fun compareTo(other: BookRating): Int = value.compareTo(other.value)
}
