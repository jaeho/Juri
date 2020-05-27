package com.jaehochoe.juri

import com.jaehochoe.juri.annotation.JuriModel
import org.junit.Assert.*
import org.junit.Test

class JuriTest {

    @Test
    fun `toUri`() {
        val model = SearchOptions("paris", "price:desc", "book", 1)
        println(Juri.toUri(model))
        assertEquals(Juri.toUri(model), "myapp://search/book/p/1?query=paris&sort=price:desc")
    }

    @Test
    fun `fromUri`() {
        val model = SearchOptions("paris", "price:desc", "book", 1)
        println(Juri.fromUri("myapp://search/book/p/1?query=paris&sort=price:desc", SearchOptions::class.java))
        assertEquals(model, Juri.fromUri("myapp://search/book/p/1?query=paris&sort=price:desc", SearchOptions::class.java))
    }

    @Test
    fun `getValues`() {
        val values = Juri.values("myapp://search/book/p/1?query=paris&sort=price:desc&page=1", SearchOptions::class.java)
        println(values)
        assertEquals(values["query"], "paris")
        assertEquals(values["sort"], "price:desc")
        assertEquals(values["category"], "book")
        assertEquals(values["page"], 1)
    }

}

@JuriModel(scheme = "myapp", host = "search", path = "/{category}/p/{page}")
data class SearchOptions(
    val query: String,
    val sort: String?,
    val category: String?,
    val page: Int
)