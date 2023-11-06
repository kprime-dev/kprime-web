package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.term.LabelField
import org.junit.Test
import kotlin.test.assertEquals

class LabelFieldTest {

    @Test
    fun test_column() {
        // given
        val label = LabelField("Actor")
        // then
        assertEquals(":Actor",label.toString())
    }

    @Test
    fun test_underscore() {
        // given
        val label = LabelField("_")
        // then
        assertEquals("_",label.toString())
    }

    @Test
    fun test_prefix() {
        // given
        val label = LabelField("rdf:Actor")
        // then
        assertEquals("rdf",label.prefix())
    }

    @Test
    fun test_no_prefix() {
        // given
        val label = LabelField("Actor")
        // then
        assertEquals("",label.prefix())
    }

    @Test
    fun test_suffix() {
        // given
        val label = LabelField("rdf:Actor")
        // then
        assertEquals("Actor",label.suffix())
    }

    @Test
    fun test_no_suffix() {
        // given
        val label = LabelField("Actor")
        // then
        assertEquals("Actor",label.suffix())
    }

}