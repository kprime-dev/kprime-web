package it.unibz.krdb.kprime.generic.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import it.unibz.krdb.kprime.view.controller.StoryController
import org.junit.Test
import kotlin.test.assertEquals

class JacksonToListTest {

    @Test
    fun test_deserialize_list_of_notes() {
        // given
        val notesText = """
[{"id":1,"title":"# Fact Checks","marked":"<h1 id=\"fact-checks\">Fact Checks</h1>\n","completed":false,"commandResult":"","commandFailure":""},{"id":8,"title":"Start with a fact:\n\nxxcaaa","marked":"<p>Start with a fact:</p>\n<p>xxcaaa</p>\n","completed":false,"commandResult":"","commandFailure":""},{"id":11,"title":">add-fact Customer orders Product","marked":"<blockquote>\n<p>add-fact Customer orders Product</p>\n</blockquote>\n","completed":false,"commandResult":"","commandFailure":""},{"id":14,"title":">tables","marked":"<blockquote>\n<p>tables</p>\n</blockquote>\n","completed":false,"commandResult":"","commandFailure":""},{"id":17,"title":">check-fact orders orders '1' Customer 'Gigi' orders Product 'Fruitx'","marked":"<blockquote>\n<p>check-fact orders orders &#39;1&#39; Customer &#39;Gigi&#39; orders Product &#39;Fruitx&#39;</p>\n</blockquote>\n","completed":false,"commandResult":"","commandFailure":""},{"id":20,"title":">check-fact orders orders '2' Customer 'Remigio' orders Product 'Ananas'","marked":"<blockquote>\n<p>check-fact orders orders &#39;2&#39; Customer &#39;Remigio&#39; orders Product &#39;Ananas&#39;</p>\n</blockquote>\n","completed":false,"commandResult":"","commandFailure":""}]
        """.trimIndent()
        // when
        val notes = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(notesText,Array<StoryController.Note>::class.java)
        // then
        assertEquals(6,notes.size)
    }
}