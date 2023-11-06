package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.view.controller.StoryController
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class StoryServiceTest {

    @Test @Ignore
    fun test_indexing() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        cmd.indexStories("/home/nipe/Dropbox/Diary/",
        "/home/nipe/.kprime/storyindex/",
        true)

    }

    @Test @Ignore
    fun test_search_video() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        val result =
            cmd.findTextAsString("/home/nipe/Dropbox/Diary/",
            cmd.findText("/home/nipe/Dropbox/Diary/", "\" video \"","/home/nipe/.kprime/storyindex/"))
        // then
        assertEquals("""
            /home/nipe/Dropbox/Diary/2017/11/20171113.md 1.2321392
            /home/nipe/Dropbox/Diary/2018/03/20180308-giuseppe-gatti.md 1.2123389
            /home/nipe/Dropbox/Diary/2018/10/20181006.md 1.1595877
            /home/nipe/Dropbox/Diary/2017/04/20170402.md 1.151369
            /home/nipe/Dropbox/Diary/2018/06/20180624-bis.md 1.1507809
            /home/nipe/Dropbox/Diary/2018/07/20180708.md 1.1388439
            /home/nipe/Dropbox/Diary/2018/09/20180916.md 1.1309032
            /home/nipe/Dropbox/Diary/2017/08/20170814.md 1.1209834
            /home/nipe/Dropbox/Diary/2017/09/20170925.md 1.1183826
            /home/nipe/Dropbox/Diary/2017/12/20171213.md 1.1183826
        """.trimIndent(),result)
    }

    @Test @Ignore
    fun test_search_video_software_company() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        val result =
            cmd.findTextAsString("/home/nipe/Dropbox/Diary/",
            cmd.findText("/home/nipe/Dropbox/Diary/", "\" video \" +Software Company","/home/nipe/.kprime/storyindex/"))
        // then
        assertEquals("""
            /home/nipe/Dropbox/Diary/2018/10/20181006.md 4.2236814
            /home/nipe/Dropbox/Diary/2017/07/20170728.md 3.1376977
            /home/nipe/Dropbox/Diary/2018/02/20180210.md 3.06685
            /home/nipe/Dropbox/Diary/2017/02/20170218.md 3.061657
            /home/nipe/Dropbox/Diary/2017/09/20170916.md 2.9656446
            /home/nipe/Dropbox/Diary/2018/09/20180922.md 2.8892686
            /home/nipe/Dropbox/Diary/2017/10/20171008.md 2.4399939
            /home/nipe/Dropbox/Diary/2018/11/20181127.md 2.2433448
            /home/nipe/Dropbox/Diary/2019/01/20190103.md 2.1533175
            /home/nipe/Dropbox/Diary/2018/09/20180916.md 2.1207433
        """.trimIndent(),result)
    }

    @Test @Ignore
    fun test_search_podcast() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        val result =
            cmd.findTextAsString("/home/nipe/Dropbox/Diary/",
            cmd.findText("/home/nipe/Dropbox/Diary/", "podcast", "/home/nipe/.kprime/storyindex/"))
        // then
        assertEquals("""
            /home/nipe/Dropbox/Diary/2018/01/20180127.md 3.2469897
            /home/nipe/Dropbox/Diary/2018/08/20180817.md 2.0983567
            /home/nipe/Dropbox/Diary/2018/12/20181223.md 2.0983567
            /home/nipe/Dropbox/Diary/2018/07/20180703.md 1.7699056
            /home/nipe/Dropbox/Diary/2017/02/20170218.md 0.7469158
        """.trimIndent(),result)
    }

    @Test @Ignore
    fun test_search_combination() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        val result =
            cmd.findTextAsString("/home/nipe/Dropbox/Diary/",
            cmd.findText("/home/nipe/Dropbox/Diary/", "+podcast +college", "/home/nipe/.kprime/storyindex/")
            )
        // then
        assertEquals("""
            /home/nipe/Dropbox/Diary/2018/01/20180127.md 6.2011886
        """.trimIndent(),result)
    }

    @Test
    fun test_from_story_path_to_url() {
        // given
        val contextMock = ContextMocker.withEmptyDatabase()
        val cmd = contextMock.pool.storyService
        // when
        val result = cmd.computeStoryUrl("/home/nipe/Dropbox/Diary/2018/01/20180127.md","/home/nipe/Dropbox/Diary/")
        // then
        assertEquals("/noteview.html?pr=diary&tr=2018___01&tf=20180127.md",result)
    }

    @Test
    fun test_notes_extraction_from_text_lines() {
        // given
        val text = """
            # First
            >help
            
            >list
            """.trimIndent()
        // when
        val result = StoryService.notesFromMarkdown(text,true)
        // then
        assertEquals("# First",result[0].title)
        assertEquals("""
            >help

            >list
        """.trimIndent(),result[1].title)
    }

    @Test
    fun test_notes_extraction_from_text_lines_with_separator() {
        // given
        val text = """
            # First
            >help
            ---
            >list
        """.trimIndent()
        // when
        val result = StoryService.notesFromMarkdown(text,true)
        // then
        assertEquals("# First",result[0].title)
        assertEquals("""
            >help
            
        """.trimIndent(),result[1].title)
        assertEquals("""
            >list
        """.trimIndent(),result[2].title)
    }

    @Test
    fun test_notes_extraction_from_text_lines_with_success() {
        // given
        val text = """
            # First
            >help
            [result:[1]<p>Ok. 13 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>]]
            ---
            >list
            
            [failure:[1]<p>Ok. 13 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>]]
        """.trimIndent()
        // when
        val result = StoryService.notesFromMarkdown(text,true)
        // then
        assertEquals("# First",result[0].title)
        println(result[1])
        assertEquals("""
            >help
            
        """.trimIndent(),result[1].title)
        assertEquals("[1]<p>Ok. 13 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>"
            .trimIndent(),result[1].commandResult)
        assertEquals("""
            >list
            
        """.trimIndent(),result[2].title)
    }

    @Test
    fun test_notes_extraction_from_text_lines_with_one_separator() {
        // given
        val text = """
            # First
            >help
            [result:[1]<p>Ok. 13 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>]]
            ---
            >list
            [failure:[1]<p>Ko. 23 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>]]
        """.trimIndent()
        // when
        val result = StoryService.notesFromMarkdown(text,true)
        // then
        assertEquals("# First",result[0].title)
        assertEquals("""
            >help
            
        """.trimIndent(),result[1].title)
        assertEquals("[1]<p>Ok. 13 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>"
            .trimIndent(),result[1].commandResult)
        assertEquals("""
            >list
        """.trimIndent(),result[2].title)
        assertEquals("[1]<p>Ko. 23 terms.<br>Customer table<br>Customer.id_Customer column<br>Customer_orders_Product table<br>Customer_orders_Product.id_Customer column<br>Customer_orders_Product.id_Product column<br>Customer_orders_Product.id_orders column<br>Person table<br>Person.Name column<br>Person.Surname column<br>Person.id_Person column<br>Product table<br>Product.id_Product column<br>gigi mapping</p>"
            .trimIndent(),result[2].commandFailure)
    }

    @Test
    fun test_to_file_content_with_one_image() {
        // given
        val content = "abc ![xyz](ABC.jpg) def"
        // when
        val newContent = StoryService.toFileContentWithImages(content,"kprime-case-confucius")
        // then
        assertEquals("abc ![xyz](/project/kprime-case-confucius/file/___/ABC.jpg) def",newContent)
    }

    @Test
    fun test_to_file_content_with_one_image_with_dir() {
        // given
        val content = "abc ![xyz](rino/gino/ABC.jpg) def"
        // when
        val newContent = StoryService.toFileContentWithImages(content,"kprime-case-confucius")
        // then
        assertEquals("abc ![xyz](/project/kprime-case-confucius/file/rino___gino/ABC.jpg) def",newContent)
    }

    @Test
    fun test_to_file_content_with_two_images() {
        // given
        val content = "abc ![xyz1](gino/ABC.jpg) def  ![xyz2](gino/123.jpg) ghi"
        // when
        val newContent = StoryService.toFileContentWithImages(content,"kprime-case-confucius")
        // then
        assertEquals("abc ![xyz1](/project/kprime-case-confucius/file/gino/ABC.jpg) def  ![xyz2](/project/kprime-case-confucius/file/gino/123.jpg) ghi",newContent)
    }

    @Test
    fun test_computeMarkdownFrom() {
        // given
        val notes = listOf(
            StoryController.Note(1,"# Titolo","",false),
            StoryController.Note(2,"Contenuto","",false),
            StoryController.Note(3,">tables","",false),
        )
        // when
        val computedMarkdown = StoryService.computeMarkdownFrom(notes)
        // then
        assertEquals("""
            # Titolo
            
            ---
            Contenuto
            
            ---
            >tables
            
            ---
            
        """.trimIndent(),computedMarkdown)

        // when
        val computedNotes = StoryService.notesFromMarkdown(computedMarkdown, true)
        // then
        for (note in computedNotes) {
            println(note)
        }
        assertEquals("# Titolo",computedNotes[0].title)
        assertEquals("Contenuto",computedNotes[1].title)
        assertEquals(">tables",computedNotes[2].title)
        assertEquals(3,computedNotes.size)
    }

    @Test
    fun test_computeMarkdownWithResultFrom() {
        // given
        val notes = listOf(
            StoryController.Note(1, "# Titolo", "", false, commandResult = "[1]<p>Ok. 13 terms."),
            StoryController.Note(2, "Contenuto", "", false,"","It's wrong."),
            StoryController.Note(3, ">tables", "", false),
        )
        // when
        val computedMarkdown = StoryService.computeMarkdownFrom(notes)
        // then
        assertEquals(
            """
            # Titolo
            [result:[1]<p>Ok. 13 terms.]]
            
            ---
            Contenuto
            [failure:It's wrong.]]
            
            ---
            >tables
            
            ---
            
        """.trimIndent(), computedMarkdown
        )
    }

    @Test
    fun test_notesFromMarkdown() {
        // given
        val computedMarkdown = """
            # Titolo
            
            Testo
            
            Bianco
            
            Nero
            
            ---
            Contenuto
            
            ---
            >tables
            
            ---
            
        """.trimIndent()
        // when
        val computedNotes = StoryService.notesFromMarkdown(computedMarkdown, true)
        // then
        for (note in computedNotes) {
            println(note)
        }
        assertEquals("# Titolo",computedNotes[0].title)
        assertEquals("""
            Testo
            
            Bianco
            
            Nero
        """.trimIndent(),computedNotes[1].title)
        assertEquals("Contenuto",computedNotes[2].title)
        assertEquals(">tables",computedNotes[3].title)
        assertEquals(4,computedNotes.size)

    }
}