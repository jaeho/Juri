package com.jaehochoe.juri

import com.jaehochoe.juri.annotation.JuriField
import com.jaehochoe.juri.annotation.JuriIgnore
import com.jaehochoe.juri.annotation.JuriModel
import com.jaehochoe.juri.annotation.JuriOnRestoreModel
import org.junit.Assert.*
import org.junit.Test

class JuriTest {

    @Test
    fun `toUri`() {
        assertEquals(Juri.toUri(SearchOptions("paris", "price:desc", "book", SearchOptions.Genre.COMICS, 1)), "myapp://search/book/p/1?genre=comics&query=paris&sort=price:desc")
    }

    @Test
    fun `fromUri`() {
        assertEquals(SearchOptions("paris", "price:desc", "book", SearchOptions.Genre.MAGAZINE, 1), Juri.fromUri("myapp://search/book/p/1?&genre=magazine&query=paris&sort=price:desc", SearchOptions::class.java))
    }

    @Test
    fun `getValues`() {
        Juri.values("myapp://search/book/p/1?query=paris&sort=price:desc&page=1", SearchOptions::class.java).let { values ->
            assertEquals(values["query"], "paris")
            assertEquals(values["sort"], "price:desc")
            assertEquals(values["category"], "book")
            assertEquals(values["page"], 1)
        }
    }

}

@JuriModel(scheme = "myapp", host = "search", path = "/{category}/p/{page}")
data class SearchOptions(
        val query: String,
        val sort: String?,
        val category: String?,
        @JuriIgnore var genre: Genre? = Genre.MAGAZINE,
        val page: Int
) {
    @JuriField("genre") val genreValue = when(genre) {
        Genre.MAGAZINE -> "magazine"
        else -> "comics"
    }

    @JuriOnRestoreModel
    fun onRestore() {
        genre = when(genreValue) {
            "magazine" -> Genre.MAGAZINE
            else -> Genre.COMICS
        }
    }

    enum class Genre {
        MAGAZINE,
        COMICS
    }
}