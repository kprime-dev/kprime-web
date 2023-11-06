package it.unibz.krdb.kprime.domain.demo1_as_table

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

/*
Given
table1: SSN,Phone
table3: SSN,Name
table4: SSN,DepName,DepAddress

PK[table1]:SSN,Phone
PK[table3]:SSN
PK[table4]:SSN

DOUBLE_INC[table4,table4]:DepName,DepAddress
DOUBLE_INC2[table1,table4]:SSN,SSN
DOUBLE_INC3[table3,table4]:SSN,SSN
DOUBLE_INC4[table1,table3]:SSN,SSN

When
trigger HD1:
LHS=,
RHS=,
REST=,
CONSTR=
RHS'=_

horizontal decomposition for nullable table4:DepName,DepAddress

changeset
add table3:SSN,Name
add table5:SSN(DepName,DepAdd=null)
add table6:SSN,DepName,DepAdd

add PK5[table5]:SSN
add PK6[table6]:SSN
add DOUBLE_INC51[table3,table5]:SSN,SSN
add DOUBLE_INC52[table3,table6]:SSN,SSN
add DOUBLE_INC61[table1,table5]:SSN,SSN
add DOUBLE_INC62[table1,table6]:SSN,SSN

remove table4
remove mapping4
remove PK4 table4
remove DOUBLE_INC
remove DOUBLE_INC2
remove DOUBLE_INC3

add mapping table5 select SSN where DepName is null and DepAddress is null
add mapping table6 select SSN where DepName not null and DepAddress not null

Then
database

table1:SSN,Phone
table3:SSN,Name
table5:SSN
table6:SSN,DepName,DepAdd

PK1[table1]:SSN,Phone
PK3[table3]:SSN
PK5[table5]:SSN
PK6[table6]:SSN

DOUBLE_INC2[table1,table2]:SSN,SSN
DOUBLE_INC51[table3,table5]:SSN,SSN // splitted from table4 to new table5,table6
DOUBLE_INC52[table3,table6]:SSN,SSN
DOUBLE_INC61[table1,table5]:SSN,SSN // splitted from table4 to new table5,table6
DOUBLE_INC61[table1,table6]:SSN,SSN

mappings
table1 : select SSN,Phone from table0
table3 : select SSN,Name from table0
table5 : select SSN from table0 where DepName is null,DepAdd is null
table6 : select SSN,DepName,DepAdd from table0 where DepName is not null,DepAdd is not null

reverse mappings
table0 : select * from table5
           join table1 on table5.SSN=table1.SSN
           join table3 on table5.SSN=table3.SSN
            union
        select * from table6
           join table1 on table6.SSN=table1.SSN
           join table3 on table6.SSN=table3.SSN
 */
class CmdParserDemo1_3Test: TraceParserTest() {

    /*
        table1: SSN,Phone
        table3: SSN,Name
        table4: SSN,DepName,DepAddress
        PK[table1]:SSN,Phone
        PK[table3]:SSN
        PK[table4]:SSN
        DOUBLE_INC[table4,table4]:DepName,DepAddress
        DOUBLE_INC2[table1,table4]:SSN,SSN
        DOUBLE_INC3[table3,table4]:SSN,SSN
        DOUBLE_INC4[table1,table3]:SSN,SSN
     */
    internal fun demo1_3_given(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-table table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-table table3:SSN,Name", context).first)
        checkOk(parser.parse("add-table table4:SSN,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-key table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-key table3:SSN", context).first)
        checkOk(parser.parse("add-key table4:SSN", context).first)
        checkOk(parser.parse("add-double-inc table4:DepName<->table4:DepAddress", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table4:SSN", context).first)
        checkOk(parser.parse("add-double-inc table3:SSN<->table4:SSN", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table3:SSN", context).first)
        checkOk(parser.parse("add-mapping table1 select SSN,Phone from table0", context).first)
        checkOk(parser.parse("add-mapping table3 select SSN,Name from table0", context).first)
        checkOk(parser.parse("add-mapping table4 select SSN,DepName,DepAddress from table0", context).first)
    }

    @Test
    fun test_changeset_demo1_3_given() {
       // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_3_given(parser, context)
        // then
        assertEquals(3, context.env.database.schema.tables?.size)
        //assertEquals(7,context.env.database.schema.constraints?.size)
        assertEquals(0, context.env.database.schema.functionals().size)
        assertEquals(4, context.env.database.schema.constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size)
        assertEquals(0, context.env.database.schema.constraintsByType(Constraint.TYPE.MULTIVALUED).size)
        assertEquals(3, context.env.database.mappings?.size)
    }

    /*
        changeset
        add table5:SSN(DepName,DepAdd=null)
        add table6:SSN,DepName,DepAdd

        add PK5[table5]:SSN
        add PK6[table6]:SSN
        add DOUBLE_INC51[table3,table5]:SSN,SSN
        add DOUBLE_INC52[table3,table6]:SSN,SSN
        add DOUBLE_INC61[table1,table5]:SSN,SSN
        add DOUBLE_INC62[table1,table6]:SSN,SSN

        remove table4
        remove mapping4
        remove PK4 table4
        remove DOUBLE_INC
        remove DOUBLE_INC2
        remove DOUBLE_INC3

        add mapping table5 select SSN where DepName is null and DepAddress is null
        add mapping table6 select SSN where DepName not null and DepAddress not null
     */
    internal fun demo1_3_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-cs-table table5:SSN", context).first)
        checkOk(parser.parse("add-cs-table table6:SSN,DepName,DepAddress", context).first)

        checkOk(parser.parse("add-cs-key table5:SSN", context).first)
        checkOk(parser.parse("add-cs-key table6:SSN", context).first)
        checkOk(parser.parse("add-cs-inclusion table5:SSN-->table1:SSN", context).first)
        checkOk(parser.parse("add-cs-foreign-key table5:SSN-->table3:SSN", context).first)
        checkOk(parser.parse("add-cs-inclusion table6:SSN-->table1:SSN", context).first)
        checkOk(parser.parse("add-cs-foreign-key table6:SSN-->table3:SSN", context).first)
        checkOk(parser.parse("add-cs-functional table6:DepName-->table6:DepAddress", context).first)
        checkOk(parser.parse("add-cs-functional table6:DepAddress-->table6:DepName", context).first)

        checkOk(parser.parse("del-cs-table table4", context).first)
        checkOk(parser.parse("del-cs-mapping table4", context).first)

        checkOk(parser.parse("add-cs-mapping table5 select SSN from table0 where DepName is null and DepAddress is null", context).first)
        checkOk(parser.parse("add-cs-mapping table6 select SSN,DepName,DepAddress from table0 where DepName is not null and DepAddress is not null", context).first)
    }

    @Test
    fun test_changeset_demo1_3_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_3_given(parser,context)
        assertEquals(0, context.env.changeSet.size())
        // when
        demo1_3_when(parser, context)
        // then
        assertEquals(14 + 1, context.env.changeSet.size()) // 12+1 del-table-4 imply del-constraints-table-4
        assertEquals(1, context.env.changeSet.dropTable.size)
        assertEquals(1, context.env.changeSet.dropMapping.size)
        assertEquals(2, context.env.changeSet.createMapping.size)
        assertEquals(2, context.env.changeSet.createTable.size)
    }

    @Test
    /*
        database
        table1:SSN,Phone
        table3:SSN,Name
        table5:SSN
        table6:SSN,DepName,DepAdd

        PK1[table1]:SSN,Phone
        PK3[table3]:SSN
        PK5[table5]:SSN
        PK6[table6]:SSN

        DOUBLE_INC2[table1,table2]:SSN,SSN
        DOUBLE_INC51[table3,table5]:SSN,SSN // splitted from table4 to new table5,table6
        DOUBLE_INC52[table3,table6]:SSN,SSN
        DOUBLE_INC61[table1,table5]:SSN,SSN // splitted from table4 to new table5,table6
        DOUBLE_INC61[table1,table6]:SSN,SSN

        mappings
        table1 : select SSN,Phone from table0
        table3 : select SSN,Name from table0
        table5 : select SSN from table0 where DepName is null,DepAdd is null
        table6 : select SSN,DepName,DepAdd from table0 where DepName is not null,DepAdd is not null
     */
    fun test_changeset_demo1_3_then() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_3_given(parser,context)
        demo1_3_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNull(context.env.database.schema.table("table0"))
        assertNull(context.env.database.schema.table("table4"))
        assertNotNull(context.env.database.schema.table("table1"))
        assertNotNull(context.env.database.schema.table("table3"))
        assertNotNull(context.env.database.schema.table("table5"))
        assertNotNull(context.env.database.schema.table("table6"))

        assertNull(context.env.database.mapping("table4"))
        assertNotNull(context.env.database.mapping("table1"))
        assertNotNull(context.env.database.mapping("table3"))
        assertNotNull(context.env.database.mapping("table5"))
        assertNotNull(context.env.database.mapping("table6"))
        assertEquals("DepName is not null and DepAddress is not null",
                context.env.database.mapping("table6")?.select?.where?.condition)
    }

    @Test
    fun test_changeset_demo1_3_chageset_sql() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_3_given(parser,context)
        demo1_3_when(parser,context)
        // when
        val sql = SQLizeCreateUseCase().createTableMappings(context.env.changeSet)
        // then
        assertEquals(8, sql.size)
        assertEquals("CREATE TABLE public.table5 AS\n" +
                "SELECT SSN\n" +
                "FROM   table0\n" +
                "WHERE DepName is null and DepAddress is null", sql[0])
        assertEquals("CREATE TABLE public.table6 AS\n" +
                "SELECT SSN,DepName,DepAddress\n" +
                "FROM   table0\n" +
                "WHERE DepName is not null and DepAddress is not null", sql[1])
        assertEquals("ALTER TABLE table5 ALTER COLUMN SSN Varchar NOT NULL", sql[2])// double-inc
        assertEquals("ALTER TABLE table5 ADD PRIMARY KEY (SSN)", sql[3])
        assertEquals("ALTER TABLE table6 ALTER COLUMN SSN Varchar NOT NULL", sql[4])// double-inc
        assertEquals("ALTER TABLE table6 ADD PRIMARY KEY (SSN)", sql[5])
    }

}