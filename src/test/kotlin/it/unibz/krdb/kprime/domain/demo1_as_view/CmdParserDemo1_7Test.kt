package it.unibz.krdb.kprime.domain.demo1_as_view

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CmdParserDemo1_7Test: TraceParserTest() {

//    internal fun demo1_7_given(parser: TraceParser, context: TraceContext) {
//        // TODO("Not yet implemented")
//    }

    @Test
    fun test_split_to_carm() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
//        demo1_7_given(parser,context)
        // then

    }

//    internal fun demo1_7_when(parser: TraceParser,context: TraceContext) {}

    @Test
    fun test_changeset_demo1_7_when() {

    }

    @Test
    // database with OIDs tables and double inclusions to relation and reference tables.
    fun test_changeset_demo1_7_then() {
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
//        demo1_7_given(parser,context)
//        demo1_7_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNull(context.env.database.schema.table("table0"))

    }

}