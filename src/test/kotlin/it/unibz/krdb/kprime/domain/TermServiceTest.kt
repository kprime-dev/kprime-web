package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser
import unibz.cs.semint.kprime.domain.db.Table
import kotlin.test.assertEquals

class TermServiceTest {

    @Test
    fun test_expand_prefix() {
        // given
        val prefixMap : Map<Prefix,Namespace> = mapOf("schema:" to "http://schema.org/",
                                                        "foaf:" to "http://foaf.org/")
        // when
        val expandPrefix1 = TermService.expandPrefix(type = "schema:Person", prefixMap)
        // then
        assertEquals("http://schema.org/Person",expandPrefix1)

        // when
        val expandPrefix2 = TermService.expandPrefix(type = "foaf:Person", prefixMap)
        // then
        assertEquals("http://foaf.org/Person",expandPrefix2)

        // when
        val expandPrefix3 = TermService.expandPrefix(type = "dod:Person", prefixMap)
        // then
        assertEquals("dod:Person",expandPrefix3)
    }

    @Test
    fun test_all_terms_expanded() {
        // given
        val allTerms = listOf(
                Term("name","category","relation","schema:Person","url","description","labels"),
                Term("name","category","relation","dod:Casa","url","description","labels"),
        Term("name","category","relation","foaf:Amico","url","description","labels")
        )
        val vocabulary = listOf(
                Vocabulary("schema:","http://schema.org/","description","reference"),
                Vocabulary("foaf:","http://foaf.org/","description","reference")
        )
        // when
        val allTermsExpanded = TermService.expandAllTerms(allTerms, vocabulary)
        // then
        assertEquals("http://schema.org/Person",allTermsExpanded[0].typeExpanded)
        assertEquals("dod:Casa",allTermsExpanded[1].typeExpanded)
        assertEquals("http://foaf.org/Amico",allTermsExpanded[2].typeExpanded)
    }

    @Test
    fun test_terms_csv_read() {
        // given
        // when
        val terms = TermService
            .parseResourceCSV("/csv/terms.csv")
        // then
        assertEquals(1,terms.size)
        assertEquals("term1",terms[0].name)
        assertEquals("cat1",terms[0].category)
        assertEquals("url1",terms[0].url)
    }

    @Test
    @Ignore
    fun test_terms_file_read() {
        //given
        // when
        val terms = TermService
            .parseFileCSV("/home/nipe/Workspaces/semint-kprime-webapp/src/test/resources/csv/terms.csv")
        // then
        assertEquals(1,terms.size)
        assertEquals("term1",terms[0].name)
        assertEquals("cat1",terms[0].category)
        assertEquals("url1",terms[0].url)
    }

    @Test
    fun test_terms_ttl_read() {
        // given
        // when
        val terms = TermService
            .parseResourceTTL("/ttl/csvw.ttl")
        // then
        //println(terms)
        assertEquals(203,terms.size)
        assertEquals("Cell",terms[0].name)
        assertEquals("",terms[0].category)
        assertEquals("",terms[0].url)
        assertEquals("""
            A Cell represents a cell at the intersection of a Row and a Column within a Table.
            rdf-schema#isDefinedBy csvw#
            """.trimIndent()
            ,terms[0].description)
    }


    private fun oldExtractTermsFromTableColums(table: Table, tableTerms: MutableMap<String, Term>) {
        for (col in table.columns) {
            val colTableName = "${table.name}.${col.name}"
            tableTerms.putIfAbsent(colTableName, Term(
                    colTableName,
                    "column",
                    "",
                    col.type?:"",
                    "",
                    "",
                    col.labels ?: ""
            )
            )
        }
    }


    @Test
    fun test_extractTermsFromTableColums() {
        // given
        val table = SchemaCmdParser.parseTable("person:name,surname")
        val terms = mutableMapOf(
                "name1" to Term("name1","category","relation","schema:Person","url","description","labels"),
                "name2" to Term("name2","category","relation","schema:Person","url","description","labels"),
        )
        val terms2 = mutableMapOf(
                "name1" to Term("name1","category","relation","schema:Person","url","description","labels"),
                "name2" to Term("name2","category","relation","schema:Person","url","description","labels"),
        )
        // when
        TermService.extractTermsFromTableColums(table,terms)
        oldExtractTermsFromTableColums(table,terms2)
        // then
        println(terms)
        println(terms2)
    }
}