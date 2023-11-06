package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.domain.term.Term
import org.junit.Test
import kotlin.test.assertEquals

class TermHashSetTest {

    @Test
    fun test_term_set_add() {
        // given
        val set = HashSet<Term>()
        // when
        set.add(
            Term(
                "alfa",
                "beta",
                "",
                "",
                "",
                "",
                "")
        )
        set.add(
            Term(
                "alfa",
                "gamma",
                "",
                "",
                "",
                "",
                "")
        )
        set.add(
            Term(
                "zeta",
                "delta",
                "",
                "",
                "",
                "",
                "")
        )
        assertEquals(2,set.size)
        assertEquals("delta",set.first { it.name=="zeta" }.category)
        assertEquals("beta",set.first { it.name=="alfa" }.category)
    }
}