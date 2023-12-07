package cz.upce.personallibrary.model

import java.io.Serializable

data class BookRating(val value: Float) : Comparable<BookRating>, Serializable {
    init {
        if (value < 1.0f || value > 5.0f) {
            throw IllegalArgumentException("Rating out of range")
        }
    }

    override fun toString() = "$value"

    override fun compareTo(other: BookRating): Int = value.compareTo(other.value)
}
