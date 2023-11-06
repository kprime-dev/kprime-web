package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import org.junit.Test
import kotlin.test.assertEquals

class VocabularyTest {

    @Test
    fun test_check_vocabulary_known_prefixes() {
        // given
        val tokens = listOf("xsd:aaa","schema:org","rdf:wwww","xml:123")
        val knownPrefixes = listOf("xsd:","rdf:")
        // when
        val checkUnknownPrefixes = Vocabulary.checkUnknownPrefixes(tokens, knownPrefixes)
        // then
        assertEquals(listOf("schema:","xml:"),checkUnknownPrefixes)
    }
}