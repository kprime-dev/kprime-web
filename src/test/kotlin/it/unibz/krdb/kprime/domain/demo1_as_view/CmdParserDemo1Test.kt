package it.unibz.krdb.kprime.domain.demo1_as_view

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Ignore
import org.junit.Test

class CmdParserDemo1Test : TraceParserTest() {

    @Test
    fun test_demo1() {
        // given a ram-H2-source and a kp-parser
        val context = ContextMocker.withH2MemDatabase()
        val parser = CmdParser()
        // create table0
        CmdParserDemo1_0Test().demo1_table0(parser,context)
        val selectTable0 = parser.parse("select * from table0", context)
        checkOk(selectTable0.first)
//        println(selectTable0.first.message)

        // split MVD SSN->>Phone
        // mapping table1
        // mapping table2
        //TraceParserDemo1_1Test().demo1_1_given(parser,context)
        CmdParserDemo1_1Test().demo1_1_when(parser,context)
        checkOk(parser.parse("changeset-apply",context).first)
        val selectTable1 = parser.parse("select table1", context)
        checkOk(selectTable1.first)
//        println(selectTable1.first.message)

        // split FD SSN-->Name
        // mapping table3
        // mapping table4
        //TraceParserDemo1_2Test().demo1_2_given(parser,context)
        CmdParserDemo1_2Test().demo1_2_when(parser,context)
        checkOk(parser.parse("changeset-apply",context).first)
        val selectTable4 = parser.parse("select table4", context)
        checkOk(selectTable4.first)
//        println(selectTable4.first.message)

/*
        // split H DepName not null
        // mapping table5
        // mapping table6
        //TraceParserDemo1_3Test().demo1_3_given(parser,context)
        TraceParserDemo1_3Test().demo1_3_when(parser,context)
        checkOk(parser.parse("changeset-apply",context).first)
        val selectTable6 = parser.parse("select table6", context)
        checkOk(selectTable6.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
DEPNAME: DepName2
DEPADDRESS: DepAddress2

        """.trimIndent(),selectTable6.first.message)
        // split FD SSN-->DepName
        // mapping table7
        // mapping table8
        TraceParserDemo1_5Test().demo1_5_when(parser,context)
        checkOk(parser.parse("changeset-apply",context).first)
        val selectTable8 = parser.parse("select table8", context)
        checkOk(selectTable8.first)
        assertEquals("""
-----------------------------------------------------DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------DEPNAME: DepName2
DEPADDRESS: DepAddress2

        """.trimIndent(),selectTable8.first.message)

        // POID e DOID
        // add projection AS
        TraceParserDemo1_6Test().demo1_6_when(parser,context)
        checkOk(parser.parse("changeset-apply",context).first)
        val selectTable9 = parser.parse("select table9", context)
        checkOk(selectTable9.first)
        assertEquals("""
-----------------------------------------------------POID: SSN1
SSN: SSN1
NAME: Name1
-----------------------------------------------------POID: SSN2
SSN: SSN2
NAME: Name2
-----------------------------------------------------POID: SSN3
SSN: SSN3
NAME: Name3
-----------------------------------------------------POID: SSN4
SSN: SSN4
NAME: Name4

        """.trimIndent(),selectTable9.first.message)

        // CARM : OIDs distribution and table table fragmentations ariety > 2
        //
*/
    }

}