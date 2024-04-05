package it.unibz.krdb.kprime.support

import junit.framework.TestCase.assertEquals
import kotlin.test.Ignore
import kotlin.test.Test

class MdHtmlPublisherTest {

    @Test
    fun test_translate_one_md_file_to_html() {
        // given
        val publisher = MdHtmlPublisher()
        val mdFileText = """
                title=Design with, not for. -> Team Clarifications
                date=2024-08-21
                type=post
                tags=blog,chap4,ontology
                status=published
                ~~~~~~
                
                
                ##### Chapter 4: Choose a Direction
                
                ---
                
                It’s important to discuss and vet your ontological decisions with stakeholders and users. Talking about language choices gives you a chance to test them.
                
                It may sound obvious, but it’s quite common to think something is clearly defined before talking about it with other people.
                
                A good starting point in exploring ontology is to bring everyone together to make a list of terms and concepts. Ask each person to share:
                
                    One term that they wish they knew more about
                    One term that they wish others understood better
                
                Go through each term as a group and use this as a forum for educating each other on what you know about language and context. Don’t “uh huh” your way through words you’ve never heard or don’t understand. Instead, untangle acronyms and unfamiliar phrases.
                
                If someone uses a different word than you do, ask for clarification. Why do they use that word? Get them to explain it. Complexity tends to hide in minutiae.
                
                ```
                Exercise
                
                    -> Examine one term at a time ask for clarifications.
                
                ```
                
                ![shared-terms](/img/shared-terms4_bw.jpeg)
            
        """.trimIndent()
        // when
        val htmlFileText = publisher.translate(mdFileText)
        // then
        assertEquals("""
            
        """.trimIndent(), htmlFileText)
    }

    @Test
    @Ignore
    fun test_translate_folder() {
        // given
        val publisher = MdHtmlPublisher()
        // when
        publisher.translateFolder(
            "/home/nipe/Workspaces/informatelier/books/htmsoc/content/",
            "/home/nipe/Temp/published"
        )
        publisher.assetFolder(
            "/home/nipe/Workspaces/informatelier/books/htmsoc/assets/",
            "/home/nipe/Temp/published")
    }
}