package it.unibz.krdb.kprime.domain.demo1_as_view

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.CmdParser
import it.unibz.krdb.kprime.domain.TraceParserTest
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CmdParserDemo1_5Test: TraceParserTest() {

/*
Given
Ignore table 5

table1:SSN,Phone
table3:SSN,Name
table6:SSN,DepName,DepAdd

PK1[table1]:SSN,Phone
PK3[table3]:SSN
PK6[table6]:SSN

DOUBLE_INC2[table1,table2]:SSN,SSN
DOUBLE_INC61[table1,table6]:SSN,SSN
DOUBLE_INC62[table3,table6]:SSN,SSN

mappings
table1 : select SSN,Phone from table0
table3 : select SSN,Name from table0
table6 : select SSN,DepName,DepAdd from table0 where DepName is not null,DepAdd is not null

When
trigger HD1:
LHS=,
RHS=,
REST=,
CONSTR=
RHS'=_

vertical decomposition FD table6:SSN,DepName

changeset
add table7:SSN,DepName
add table8:DepName,DepAdd

add PK7[table7]:SSN
add PK8[table8]:DepName
add DOUBLE_INC71[table3,table5]:SSN,SSN
add DOUBLE_INC72[table1,table5]:SSN,SSN
add DOUBLE_INC81[table1,table6]:SSN,SSN
add DOUBLE_INC82[table3,table6]:SSN,SSN

remove table6
remove mapping6
remove table6 constraints
    remove PK6 table6
    remove DOUBLE_INC61
    remove DOUBLE_INC62

add mapping table7 select SSN,DepName from table0 where DepName is not null
add mapping table8 select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null

Then
database

table1:SSN,Phone
table3:SSN,Name
table7:SSN,DepName
table8:DepName,DepAdd

PK1[table1]:SSN,Phone
PK3[table3]:SSN
PK7[table7]:SSN
PK8[table8]:DepName
PK9[table8]:DepAddress

DOUBLE_INC2[table1,table2]:SSN,SSN
DOUBLE_INC52[table3,table7]:SSN,SSN
DOUBLE_INC61[table1,table7]:SSN,SSN
DOUBLE_INC52[table3,table8]:SSN,DepName
DOUBLE_INC61[table1,table8]:SSN,DepName

mappings
table1 : select SSN,Phone from table0
table3 : select SSN,Name from table0
table7 : select SSN,DepName from table0 where DepName is not null
table8 : select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null

reverse mappings
table0 : ?
 */
    /*
    table1:SSN,Phone
    table3:SSN,Name
    table6:SSN,DepName,DepAddress

    PK1[table1]:SSN,Phone
    PK3[table3]:SSN
    PK6[table6]:SSN

    DOUBLE_INC2[table1,table3]:SSN,SSN
    DOUBLE_INC61[table1,table6]:SSN,SSN
    DOUBLE_INC62[table3,table6]:SSN,SSN
    DOUBLE_INC6[table6,table6]:DepName,DepAddress

    mappings
    table1 : select SSN,Phone from table0
    table3 : select SSN,Name from table0
    table6 : select SSN,DepName,DepAdd from table0 where DepName is not null,DepAdd is not null
     */
    internal fun demo1_5_given(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-table table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-table table3:SSN,Name", context).first)
        checkOk(parser.parse("add-table table6:SSN,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-key table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-key table3:SSN", context).first)
        checkOk(parser.parse("add-key table6:SSN", context).first)
        checkOk(parser.parse("add-double-inc table6:DepName<->table6:DepAddress", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table6:SSN", context).first)
        checkOk(parser.parse("add-double-inc table3:SSN<->table6:SSN", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table3:SSN", context).first)
        checkOk(parser.parse("add-mapping table1 select SSN,Phone from table0", context).first)
        checkOk(parser.parse("add-mapping table3 select SSN,Name from table0", context).first)
        checkOk(parser.parse("add-mapping table6 select SSN,DepName,DepAddress from table0 where DepName is not null and DepAddress is not null", context).first)
    }

    @Test
    fun test_changeset_demo1_5_given() {
       // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_5_given(parser, context)
        // then
        assertEquals(3, context.env.database.schema.tables?.size)
        //assertEquals(7,context.env.database.schema.constraints?.size)
        assertEquals(0, context.env.database.schema.functionals().size)
        assertEquals(4, context.env.database.schema.constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size)
        assertEquals(0, context.env.database.schema.constraintsByType(Constraint.TYPE.MULTIVALUED).size)
        assertEquals(3, context.env.database.mappings?.size)
    }

    /*
        changeset 5

        add table7:SSN,DepName
        add table8:DepName,DepAdd

        add PK7[table7]:SSN
        add PK8[table8]:DepName
        add DOUBLE_INC71[table3,table7]:SSN,SSN
        add DOUBLE_INC72[table1,table7]:SSN,SSN
        add DOUBLE_INC81[table1,table8]:SSN,SSN
        add DOUBLE_INC82[table3,table8]:SSN,SSN

        remove table6
        remove mapping6
        remove table6 constraints
            remove PK6 table6
            remove DOUBLE_INC61
            remove DOUBLE_INC62

        add mapping table7 select SSN,DepName from table0 where DepName is not null
        add mapping table8 select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null
     */
    internal fun demo1_5_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-cs-table table7:SSN,DepName", context).first)
        checkOk(parser.parse("add-cs-table table8:DepName,DepAddress", context).first)

        checkOk(parser.parse("add-cs-key table7:SSN", context).first)
        checkOk(parser.parse("add-cs-key table8:DepName", context).first)
        checkOk(parser.parse("add-cs-double-inc table3:SSN<->table7:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table1:SSN<->table7:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table3:SSN<->table8:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table1:SSN<->table8:SSN", context).first)

        checkOk(parser.parse("del-cs-table table6", context).first)
        checkOk(parser.parse("del-cs-mapping table6", context).first)

        checkOk(parser.parse("add-cs-mapping table7 select SSN,DepName from table0 where DepName is null and DepAddress is null", context).first)
        checkOk(parser.parse("add-cs-mapping table8 select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null", context).first)
    }

    @Test
    fun test_changeset_demo1_5_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_5_given(parser,context)
        assertEquals(0, context.env.changeSet.size())
        // when
        demo1_5_when(parser, context)
        // then
        assertEquals(12 + 1, context.env.changeSet.size()) // 12+1 del-table-4 imply del-constraints-table-4
        assertEquals(1, context.env.changeSet.dropTable.size)
        assertEquals(1, context.env.changeSet.dropMapping.size)
        assertEquals(2, context.env.changeSet.createMapping.size)
        assertEquals(2, context.env.changeSet.createTable.size)
    }

    @Test
    /*
        database 5

        table1:SSN,Phone
        table3:SSN,Name
        table7:SSN,DepName
        table8:DepName,DepAdd

        PK1[table1]:SSN,Phone
        PK3[table3]:SSN
        PK7[table7]:SSN
        PK8[table8]:DepName
        PK9[table8]:DepAddress

        DOUBLE_INC2[table1,table2]:SSN,SSN
        DOUBLE_INC52[table3,table7]:SSN,SSN
        DOUBLE_INC61[table1,table7]:SSN,SSN
        DOUBLE_INC52[table3,table8]:SSN,DepName
        DOUBLE_INC61[table1,table8]:SSN,DepName

        mappings
        table1 : select SSN,Phone from table0
        table3 : select SSN,Name from table0
        table7 : select SSN,DepName from table0 where DepName is not null
        table8 : select DepName,DepAddress from table0 where DepName is not null and DepAddress is not null
     */
    fun test_changeset_demo1_3_then() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_5_given(parser,context)
        demo1_5_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNull(context.env.database.schema.table("table0"))
        assertNull(context.env.database.schema.table("table4"))
        assertNull(context.env.database.schema.table("table5"))
        assertNull(context.env.database.schema.table("table6"))
        assertNotNull(context.env.database.schema.table("table1"))
        assertNotNull(context.env.database.schema.table("table3"))
        assertNotNull(context.env.database.schema.table("table7"))
        assertNotNull(context.env.database.schema.table("table8"))

        assertNull(context.env.database.mapping("table4"))
        assertNotNull(context.env.database.mapping("table1"))
        assertNotNull(context.env.database.mapping("table3"))
        assertNotNull(context.env.database.mapping("table7"))
        assertNotNull(context.env.database.mapping("table8"))
    }

    @Test
    fun test_changeset_demo1_5_chageset_sql() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_5_given(parser,context)
        demo1_5_when(parser,context)
        // when
        val sql = SQLizeCreateUseCase().createCommands(context.env.changeSet)
        // then
        assertEquals(8, sql.size)
        assertEquals("CREATE TABLE IF NOT EXISTS table7 (  SSN varchar(64) ,DepName varchar(64));", sql[0])
        assertEquals("CREATE TABLE IF NOT EXISTS table8 (  DepName varchar(64) ,DepAddress varchar(64));", sql[1])
        assertEquals("CREATE OR REPLACE VIEW public.table7 AS\n" +
                "SELECT SSN,DepName\n" +
                "FROM   table0\n" +
                "WHERE DepName is null and DepAddress is null", sql[2])
        assertEquals("CREATE OR REPLACE VIEW public.table8 AS\n" +
                "SELECT DepName,DepAddress\n" +
                "FROM   table0\n" +
                "WHERE DepName is not null and DepAddress is not null", sql[3])
        assertEquals("ALTER TABLE table7 ALTER COLUMN SSN Varchar NOT NULL", sql[4])// double-inc
        assertEquals("ALTER TABLE table7 ADD PRIMARY KEY (SSN)", sql[5])
        assertEquals("ALTER TABLE table8 ALTER COLUMN DepName Varchar NOT NULL", sql[6])// double-inc
        assertEquals("ALTER TABLE table8 ADD PRIMARY KEY (DepName)", sql[7])
    }

}