package it.unibz.krdb.kprime.domain.h2

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test

class SourceH2Test: TraceParserTest() {

    @Test
    fun test_insert_select_h2() {
        //given a ram-H2-source and a kp-parser
        val context = ContextMocker.withH2MemDatabase()
        val parser = CmdParser()
        // when create, insert and select
        checkOk(parser.parse("set-connection conn1 autocommit:true",context).first)
        checkOk(parser.parse("drop table if exists Person",context).first)
        checkOk(parser.parse("create table Person(name varchar(64),surname varchar(64))",context).first)
        checkOk(parser.parse("set-connection conn1 autocommit:false",context).first)
        checkOk(parser.parse("insert into Person values('Nino','Rossi')",context).first)
        checkOk(parser.parse("insert into Person values('Paolo','Rossi')",context).first)
        checkOk(parser.parse("set-connection conn1 autocommit:true closed:true",context).first)
        val selectResult = parser.parse("select * from Person", context)
        checkOk(selectResult.first)
        // then result is not empty
        // TODO Assert result
    }

}