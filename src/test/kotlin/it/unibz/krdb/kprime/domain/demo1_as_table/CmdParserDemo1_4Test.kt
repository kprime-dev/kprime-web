package it.unibz.krdb.kprime.domain.demo1_as_table

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test
import kotlin.test.assertEquals

class CmdParserDemo1_4Test: TraceParserTest() {

    @Test
    fun test_changeset_demo1_4_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_4_when(parser, context)
        // then
        assertEquals(0, context.env.database.schema.tables?.size)
    }

    internal fun demo1_4_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("del-cs-table table5", context).first)
        checkOk(parser.parse("del-cs-mapping table5", context).first)
    }
}
