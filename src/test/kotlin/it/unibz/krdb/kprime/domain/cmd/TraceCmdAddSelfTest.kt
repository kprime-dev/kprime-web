package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test
import kotlin.test.assertEquals

class TraceCmdAddSelfTest: TraceParserTest() {

    @Test
    fun test_add_self_to_simple_table() {
        // given
        val context = ContextMocker.withH2MemDatabase()
        val parser = CmdParser()
        checkOk(parser.parse("drop table if exists Person", context).first)
        checkOk(parser.parse("create table Person(name varchar(64),surname varchar(64))",context).first)
        checkOk(parser.parse("insert into Person values('Nino','Rossi')",context).first)
        checkOk(parser.parse("insert into Person values('Paolo','Rossi')",context).first)
        // when
        checkOk(parser.parse("add-self Person",context).first)
        // then
        val selectPerson = parser.parse("select * from Person", context)
        checkOk(selectPerson.first)
        assertEquals("""
-----------------------------------------------------NAME: Nino
SURNAME: Rossi
SELF: 1
-----------------------------------------------------NAME: Paolo
SURNAME: Rossi
SELF: 2

        """.trimIndent(),selectPerson.first.message)


    }
}