package it.unibz.krdb.kprime.domain.demo1_as_table

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListAll
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListConstraints
import it.unibz.krdb.kprime.domain.cmd.update.TraceCmdCSApply
import it.unibz.krdb.kprime.domain.cmd.update.TraceCmdSqlExecCSMappings
import it.unibz.krdb.kprime.domain.cmd.update.TraceCmdSqlExecCS
import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import kotlin.test.assertEquals

class CmdParserDemo1Test : TraceParserTest() {

    @Test
    fun test_demo1() {
        // given a ram-H2-source and a kp-parser
        val context = ContextMocker.withH2MemDatabase()
        val parser = CmdParser()

        // ------------------------------------------------ STEP ---- 0 ----------------------------
        // create table0
        CmdParserDemo1_0Test().demo1_table0(parser,context)
        val selectTable0 = parser.parse("select * from table0", context)
        checkOk(selectTable0.first)
        //println(selectTable0.first.message)

        // ------------------------------------------------ STEP ---- 1 ----------------------------
        // split MVD SSN->>Phone
        // mapping table1
        // mapping table2
        //TraceParserDemo1_1Test().demo1_1_given(parser,context)
        CmdParserDemo1_1Test().demo1_1_when(parser,context)
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCSMappings.getCmdName(),context).first)

        val select1Table1 = parser.parse("select * from table1", context)
        checkOk(select1Table1.first)
        //println(selectTable1.first.message)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
PHONE: Phone1
-----------------------------------------------------SSN: SSN2
PHONE: Phone2
-----------------------------------------------------SSN: SSN3
PHONE: Phone3
-----------------------------------------------------SSN: SSN4
PHONE: Phone4

        """.trimIndent(),select1Table1.first.message)

        val select1Table2 = parser.parse("select * from table2", context)
        checkOk(select1Table2.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
NAME: Name1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
NAME: Name2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
NAME: Name3
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN4
NAME: Name4
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),select1Table2.first.message)

        //val selectConstraint1 =
                parser.parse(TraceCmdListConstraints.getCmdName(), context)
        //println(selectConstraint1.first.message)

//        println("------------------------------------------------ STEP ---- 2 ----------------------------")
        // split FD SSN-->Name
        // mapping table3
        // mapping table4
        context.env.changeSet = ChangeSet()
        CmdParserDemo1_2Test().demo1_2_when(parser,context)
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCSMappings.getCmdName(),context).first)
        val select2Table1 = parser.parse("select * from table1", context)
        checkOk(select2Table1.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
PHONE: Phone1
-----------------------------------------------------SSN: SSN2
PHONE: Phone2
-----------------------------------------------------SSN: SSN3
PHONE: Phone3
-----------------------------------------------------SSN: SSN4
PHONE: Phone4

        """.trimIndent(),select2Table1.first.message)

        val select2Table3 = parser.parse("select * from table3", context)
        checkOk(select2Table3.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
NAME: Name1
-----------------------------------------------------SSN: SSN2
NAME: Name2
-----------------------------------------------------SSN: SSN3
NAME: Name3
-----------------------------------------------------SSN: SSN4
NAME: Name4

        """.trimIndent(),select2Table3.first.message)

        val select2Table4 = parser.parse("select * from table4", context)
        checkOk(select2Table4.first)
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
-----------------------------------------------------SSN: SSN4
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),select2Table4.first.message)

        //val selectConstraint2 =
            parser.parse(TraceCmdListAll.getCmdName(), context)
//        println(selectConstraint2.first.message)
//        println("------------------------------------------------ STEP ---- 3 ----------------------------")
        // split H DepName not null
        // mapping table5
        // mapping table6
        context.env.changeSet = ChangeSet()
        CmdParserDemo1_3Test().demo1_3_when(parser,context)
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCSMappings.getCmdName(),context).first)

        val select3Table5 = parser.parse("select * from table5", context)
        checkOk(select3Table5.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN4

        """.trimIndent(),select3Table5.first.message)

        val select3Table6 = parser.parse("select * from table6", context)
        checkOk(select3Table6.first)
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

        """.trimIndent(),select3Table6.first.message)

//        val selectConstraint3 =
        parser.parse(TraceCmdListAll.getCmdName(), context)
//        println(selectConstraint3.first.message)
//        println("------------------------------------------------ STEP ---- 4 ----------------------------")
        // ignore table

        context.env.changeSet = ChangeSet()
        CmdParserDemo1_4Test().demo1_4_when(parser,context)
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)

        val selectConstraint4 = parser.parse(TraceCmdListAll.getCmdName(), context)
//        println(selectConstraint4.first.message)
//        println("------------------------------------------------ STEP ---- 5 ----------------------------")
        // split FD SSN-->DepName
        // mapping table7
        // mapping table8
        context.env.changeSet = ChangeSet()
        CmdParserDemo1_5Test().demo1_5_when(parser,context)
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCSMappings.getCmdName(),context).first)

        val select5Table7 = parser.parse("select * from table7", context)
        checkOk(select5Table7.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
DEPNAME: DepName1
-----------------------------------------------------SSN: SSN2
DEPNAME: DepName2
-----------------------------------------------------SSN: SSN3
DEPNAME: DepName2

        """.trimIndent(),select5Table7.first.message)

        val select5Table8 = parser.parse("select * from table8", context)
        checkOk(select5Table8.first)
        assertEquals("""
-----------------------------------------------------DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------DEPNAME: DepName2
DEPADDRESS: DepAddress2

        """.trimIndent(),select5Table8.first.message)

//        val selectConstraint5 =
        parser.parse(TraceCmdListAll.getCmdName(), context)
//        println(selectConstraint5.first.message)
//        println("------------------------------------------------ STEP ---- 6 ----------------------------")
        // POID e DOID
        // add auto-inc columns and distribute surrogate key
        context.env.changeSet = ChangeSet()
//        var oidCommandsT3 = mutableListOf<String>()
//        oidCommandsT3.addAll(context.database.schema.oidForTable("table3").sqlCommands.toList())
//        context.changeSet.sqlCommands = oidCommandsT3
        context.env.changeSet.add(context.env.database.schema.oidForTable("table3"))
        checkOk(parser.parse(TraceCmdSqlExecCS.getCmdName(),context).first)

        val select6Table3 = parser.parse("select * from table3", context)
        checkOk(select6Table3.first)
        assertEquals("""
-----------------------------------------------------NAME: Name1
SIDTABLE3: 1
-----------------------------------------------------NAME: Name2
SIDTABLE3: 2
-----------------------------------------------------NAME: Name3
SIDTABLE3: 3
-----------------------------------------------------NAME: Name4
SIDTABLE3: 4

        """.trimIndent(),select6Table3.first.message)

        val select6SKEYTable3 = parser.parse("select * from SKEYtable3", context)
        checkOk(select6SKEYTable3.first)
        assertEquals("""
-----------------------------------------------------SIDTABLE3: 1
SSN: SSN1
-----------------------------------------------------SIDTABLE3: 2
SSN: SSN2
-----------------------------------------------------SIDTABLE3: 3
SSN: SSN3
-----------------------------------------------------SIDTABLE3: 4
SSN: SSN4

        """.trimIndent(),select6SKEYTable3.first.message)

            val select6Table1_1 = parser.parse("select * from table1_1", context)
            checkOk(select6Table1_1.first)
            assertEquals("""
-----------------------------------------------------SIDTABLE3: 1
PHONE: Phone1
-----------------------------------------------------SIDTABLE3: 2
PHONE: Phone2
-----------------------------------------------------SIDTABLE3: 3
PHONE: Phone3
-----------------------------------------------------SIDTABLE3: 4
PHONE: Phone4

        """.trimIndent(),select6Table1_1.first.message)


        val select6Table8 = parser.parse("select * from table8", context)
        checkOk(select6Table8.first)
        assertEquals("""
-----------------------------------------------------DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------DEPNAME: DepName2
DEPADDRESS: DepAddress2

        """.trimIndent(),select6Table8.first.message)



            val select6Table7_1 = parser.parse("select * from table7_1", context)
            checkOk(select6Table7_1.first)
            assertEquals("""
-----------------------------------------------------SIDTABLE3: 1
DEPNAME: DepName1
-----------------------------------------------------SIDTABLE3: 2
DEPNAME: DepName2
-----------------------------------------------------SIDTABLE3: 3
DEPNAME: DepName2

        """.trimIndent(),select6Table7_1.first.message)

// todo
        context.env.changeSet = ChangeSet()
//        var oidCommandsT8 = mutableListOf<String>()
//        oidCommandsT8.addAll(context.database.schema.oidForTable("table8"))
//        context.changeSet.sqlCommands = oidCommandsT8
        context.env.changeSet.add(context.env.database.schema.oidForTable("table8"))
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCS.getCmdName(),context).first)

        val select6Table7_1_1 = parser.parse("select * from table7_1_1", context)
        checkOk(select6Table7_1_1.first)
        assertEquals("""
-----------------------------------------------------SIDTABLE8: 1
SIDTABLE3: 1
-----------------------------------------------------SIDTABLE8: 2
SIDTABLE3: 2
-----------------------------------------------------SIDTABLE8: 2
SIDTABLE3: 3

        """.trimIndent(),select6Table7_1_1.first.message)


//        val selectAll =
        parser.parse(TraceCmdListAll.getCmdName(), context)
//        println(selectAll.first.message)

//        val selectConstraint6 =
        parser.parse(TraceCmdListConstraints.getCmdName(), context)
//        println(selectConstraint6.first.message)
//        println("------------------------------------------------ STEP ---- 7 ----------------------------")
        // CARM : table fragmentation ariety > 2
        //


//        println("------------------------------------------------ STEP ---- query table0 ---------------------")
        // table0 as mappings mega join
        val select8AllFrom7_1_1 = parser.parse("""
            select ssn,phone,name,depname,depaddress 
            from table7_1_1
            join table1_1 on table7_1_1.SIDTABLE3=table1_1.SIDTABLE3
            join table3 on table7_1_1.SIDTABLE3=table3.SIDTABLE3
            join SKEYtable3 on table7_1_1.SIDTABLE3=SKEYtable3.SIDTABLE3
            join SKEYtable8 on table7_1_1.sidtable8=SKEYtable8.sidtable8
            join table8 on table7_1_1.sidtable8=table8.sidtable8
        """.trimIndent(), context)
        checkOk(select8AllFrom7_1_1.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
PHONE: Phone1
NAME: Name1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
PHONE: Phone2
NAME: Name2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
PHONE: Phone3
NAME: Name3
DEPNAME: DepName2
DEPADDRESS: DepAddress2

        """.trimIndent(),select8AllFrom7_1_1.first.message)


        val select8Phones = parser.parse("select * from table1_1", context)
        checkOk(select8Phones.first)
        assertEquals("""
-----------------------------------------------------SIDTABLE3: 1
PHONE: Phone1
-----------------------------------------------------SIDTABLE3: 2
PHONE: Phone2
-----------------------------------------------------SIDTABLE3: 3
PHONE: Phone3
-----------------------------------------------------SIDTABLE3: 4
PHONE: Phone4

        """.trimIndent(),select8Phones.first.message)



    val select8Table0 = """
            select ssn,name,phone,depname,depaddress
            from SKEYtable3
            left join table7_1_1 on table7_1_1.SIDTABLE3=SKEYtable3.SIDTABLE3
            left join SKEYtable8 on SKEYtable8.sidtable8=table7_1_1.sidtable8
            left join table8 on table8.sidtable8=table7_1_1.sidtable8
            left join table3 on table3.sidtable3=SKEYtable3.sidtable3
            left join table1_1 on table1_1.sidtable3=SKEYtable3.sidtable3
        """.trimIndent()
            val select8AllFrom3 = parser.parse(select8Table0, context)
        checkOk(select8AllFrom3.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
NAME: Name1
PHONE: Phone1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
NAME: Name2
PHONE: Phone2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
NAME: Name3
PHONE: Phone3
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN4
NAME: Name4
PHONE: Phone4
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),select8AllFrom3.first.message)

        // ------------------------------------------------ STEP ---- insert ----------------------------
        // insert entity "person without dep"
        // insert entity "person with dep"
        // insert entity "dep" forbidden, "person required"
        //        Tables:
        //        table1: SSN , Phone
        //        table3: Name , sidtable3
        //        table7: SSN , DepName
        //        table8: DepAddress , sidtable8
        //        SKEYtable3: sidtable3 , SSN
        //        table1_1: sidtable3 , Phone
        //        table7_1: sidtable3 , DepName
        //        SKEYtable8: sidtable8 , DepName
        //        table7_1_1: sidtable8 , sidtable3

        val person5sqlCommands = mutableListOf(
                "insert into SKEYtable3 values(5,'SSNGino')",
                "insert into table3 values('Gino',5)",
                "insert into table1_1 values(5,'PhoneGino')",
                "insert into table7_1_1 values(5,5)"
        )
        val changeSet5 = ChangeSet()
        changeSet5.sqlCommands = person5sqlCommands
        context.env.changeSet = changeSet5
        checkOk(parser.parse(TraceCmdCSApply.getCmdName(),context).first)
        checkOk(parser.parse(TraceCmdSqlExecCS.getCmdName(),context).first)
        val select9AllFrom3 = parser.parse(select8Table0, context)
        checkOk(select9AllFrom3.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
NAME: Name1
PHONE: Phone1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
NAME: Name2
PHONE: Phone2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
NAME: Name3
PHONE: Phone3
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN4
NAME: Name4
PHONE: Phone4
DEPNAME: null
DEPADDRESS: null
-----------------------------------------------------SSN: SSNGino
NAME: Gino
PHONE: PhoneGino
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),select9AllFrom3.first.message)

        // ------------------------------------------------ STEP ---- diff table0 ----------------------
        // table0diff as query minus
        val diffSqlCommands = mutableListOf(
                "create table newtable0 as $select8Table0"
        )
        val changeSetDiff = ChangeSet()
        changeSetDiff.sqlCommands = diffSqlCommands
        context.env.changeSet = changeSetDiff
        checkOk(parser.parse(TraceCmdSqlExecCS.getCmdName(),context).first)

        val selectNewTable0 = parser.parse("select * from newtable0", context)
        checkOk(selectNewTable0.first)
        assertEquals("""
-----------------------------------------------------SSN: SSN1
NAME: Name1
PHONE: Phone1
DEPNAME: DepName1
DEPADDRESS: DepAddress1
-----------------------------------------------------SSN: SSN2
NAME: Name2
PHONE: Phone2
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN3
NAME: Name3
PHONE: Phone3
DEPNAME: DepName2
DEPADDRESS: DepAddress2
-----------------------------------------------------SSN: SSN4
NAME: Name4
PHONE: Phone4
DEPNAME: null
DEPADDRESS: null
-----------------------------------------------------SSN: SSNGino
NAME: Gino
PHONE: PhoneGino
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),selectNewTable0.first.message)

        val selectDiff = parser.parse("""
            select ssn,name,phone,depname,depaddress from newtable0 
            minus 
            select ssn,name,phone,depname,depaddress from table0
        """.trimIndent(), context)
        checkOk(selectDiff.first)
        assertEquals("""
-----------------------------------------------------SSN: SSNGino
NAME: Gino
PHONE: PhoneGino
DEPNAME: null
DEPADDRESS: null

        """.trimIndent(),selectDiff.first.message)


    }




}