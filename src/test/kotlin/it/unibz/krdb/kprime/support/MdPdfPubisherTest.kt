package it.unibz.krdb.kprime.support

import java.io.File
import kotlin.test.Ignore
import kotlin.test.Test

class MdPdfPubisherTest {

    @Test
    @Ignore
    fun test_pdf_publish() {
        // given
        val publisher = MdPdfPublisher()
        File("/home/nipe/Temp/published-pdf/").mkdirs()
        val contextPath = "/home/nipe/Workspaces/informatelier/books/htmsoc-kp/"
        val outPdfFilename = "/home/nipe/Temp/published-pdf/publishedSingle.pdf"
        val mdText = """
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
        publisher.textToPdf(mdText, outPdfFilename, contextPath)
        // then
        // file published

    }

    @Test
    @Ignore
    fun test_pdf_publish_folder() {
        // given
        val publisher = MdPdfPublisher()
        val sourceFolderName = "/home/nipe/Workspaces/informatelier/books/htmsoc-kp/"
        val targetFolderName = "/home/nipe/Temp/published-pdf/"
        val contextPath = "/home/nipe/Workspaces/informatelier/books/htmsoc-kp/"
        val contextName = "htmsoc-kp"
        val metadata = mapOf(
            MdPdfPublisher.Metadata.Author to "Author: Nicola Pedot",
            MdPdfPublisher.Metadata.Version to "Version: 1.0"
        )
        // when
        publisher.translateFolder(
            sourceFolderName,
            targetFolderName ,
            contextPath,
            contextName,
            metadata)
    }

}