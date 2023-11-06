package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddFactShortcut
import org.junit.Test
import kotlin.test.assertTrue

class TraceCmdAddFactShortcutTest {

    @Test
    fun test_isAFunctionalDefinition() {
        //given
        val cmd = ">+ Person:name-->address"
        // then
        assertTrue(TraceCmdAddFactShortcut().isAFunctionalDefinition(cmd))
        //given
        val cmd2 = "+ Person:name-->address"
        // then
        assertTrue(TraceCmdAddFactShortcut().isAFunctionalDefinition(cmd2))
    }

    @Test
    fun test_() {
        // given
        val cmd = "+ Person:name,surname"
        //then
        assertTrue(TraceCmdAddFactShortcut().isATableDefinition(cmd))
    }
}