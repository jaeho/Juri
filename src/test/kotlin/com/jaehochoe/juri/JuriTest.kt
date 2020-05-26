package com.jaehochoe.juri

import com.jaehochoe.juri.annotation.JuriModel
import org.junit.Assert.*
import org.junit.Test

class JuriTest {

    @Test
    fun `toUri`() {
        val model = SearchOptions("paris", "price:desc", 1)
        println(com.jaehochoe.juri.Juri.toUri(model))
        assertEquals(com.jaehochoe.juri.Juri.toUri(model), "myapp://search?query=paris&sort=price:desc&page=1")
    }

    @Test
    fun `fromUri`() {
        val model = SearchOptions("paris", "price:desc", 1)
        println(com.jaehochoe.juri.Juri.fromUri("myapp://search?query=paris&sort=price:desc&page=1", SearchOptions::class.java))
        assertEquals(model, com.jaehochoe.juri.Juri.fromUri("myapp://search?query=paris&sort=price:desc&page=1", SearchOptions::class.java))
    }

    @Test
    fun `getValues`() {
        val values = Juri.values("myapp://search?query=paris&sort=price:desc&page=1", SearchOptions::class.java)
        println(values)
        assertEquals(values["query"], "paris")
        assertEquals(values["sort"], "price:desc")
        assertEquals(values["page"], 1)
    }

}

@JuriModel(scheme = "myapp", host = "search")
data class SearchOptions(
    val query: String,
    val sort: String?,
    val page: Int
)