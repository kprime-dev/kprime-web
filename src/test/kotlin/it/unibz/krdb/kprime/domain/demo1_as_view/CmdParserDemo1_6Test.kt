package it.unibz.krdb.kprime.domain.demo1_as_view

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CmdParserDemo1_6Test: TraceParserTest() {

    internal fun demo1_6_given(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-table table1:SSN,Phone",context).first)
        checkOk(parser.parse("add-table table3:SSN,Name",context).first)
        checkOk(parser.parse("add-table table7:SSN,DepName",context).first)
        checkOk(parser.parse("add-table table4:DepName,DepAddress",context).first)

        checkOk(parser.parse("add-inclusion table7:SSN-->table3:SSN",context).first)
        checkOk(parser.parse("add-inclusion table7:SSN-->table1:SSN",context).first)
        checkOk(parser.parse("add-double-inc table4:DepName<->table4:DepAddress", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table3:SSN",context).first)
    }

    @Test
    fun test_demo1_6_given() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_6_given(parser, context)
        // then
        assertEquals(4, context.env.database.schema.tables?.size)
    }

    internal fun demo1_6_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-cs-table table9:POID,SSN,Name", context).first)
        checkOk(parser.parse("add-cs-table table10:DOID,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-cs-mapping table9 select SSN as POID, SSN, Name from table0", context).first)
        checkOk(parser.parse("add-cs-mapping table10 select DepName as DOID, DepName, DepAddress from table0 where DenName is not null", context).first)
    }

    @Test
    fun test_demo1_6_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        // when
        demo1_6_when(parser, context)
        // then
        assertEquals(4, context.env.changeSet.size())

    }

    @Test
    fun test_demo1_6_then() {
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_6_given(parser,context)
        demo1_6_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNull(context.env.database.schema.table("table0"))

    }
}