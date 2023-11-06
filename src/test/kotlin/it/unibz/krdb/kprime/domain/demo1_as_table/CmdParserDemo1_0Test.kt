package it.unibz.krdb.kprime.domain.demo1_as_table

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test

class CmdParserDemo1_0Test: TraceParserTest() {

    @Test
    fun test_demo1_table0_setup() {
        //given a ram-H2-source and a kp-parser
        val context = ContextMocker.withH2MemDatabase()
        val parser = CmdParser()
        // when create, insert and select
        checkOk(parser.parse("set-connection conn1 autocommit:true",context).first)
        demo1_table0(parser, context)
        checkOk(parser.parse("set-connection conn1 autocommit:true",context).first)
        val selectResult = parser.parse("select * from table0", context)
        checkOk(selectResult.first)
        // then result is not empty

    }

    internal fun demo1_table0(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("drop table if exists table0", context).first)
        checkOk(parser.parse("create table table0(SSN varchar(64), Phone varchar(64), Name varchar(64), DepName varchar(64), DepAddress varchar(64))", context).first)
        checkOk(parser.parse("insert into table0 values('SSN1', 'Phone1', 'Name1', 'DepName1', 'DepAddress1')", context).first)
        checkOk(parser.parse("insert into table0 values('SSN2', 'Phone2', 'Name2', 'DepName2', 'DepAddress2')", context).first)
        checkOk(parser.parse("insert into table0 values('SSN3', 'Phone3', 'Name3', 'DepName2', 'DepAddress2')", context).first)
        checkOk(parser.parse("insert into table0 values('SSN4', 'Phone4', 'Name4', NULL, NULL)", context).first)
    }
}