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
table2: SSN,Name,DepName,DepAddress
FD[table2]:SSN-->Name
FD[table2]:SSN-->DepName
PK[table1]:SSN,Phone
DOUBLE_INC[table2,table2]:DepName,DepAddress
DOUBLE_INC2[table1,table2]:SSN,SSN

When
trigger FD1:
LHS=SSN,
RHS=Name,
REST=DepName,DepAdd,
CONSTR=FD2,DOUBLE_INC,PK,DOUBLE_INC2
RHS'=_

vertical decomposition per la FD SSN-->Name

changeset
add table3:SSN,Name
add table4:SSN,DepName,DepAdd
add PK2[table3]:SSN
add PK3[table4]:SSN  (inherited)
add DOUBLE_INC3[table3,table4]:SSN,SSN
add DOUBLE_INC4[table1,table4]:SSN,SSN (inherited)
remove table2
remove FD:SSN-->Dep (override by PK3)

Then
database
table1:SSN,Phone
table3:SSN,Name
table4:SSN,DepName,DepAdd
PK2[table3]:SSN
PK3[table4]:SSN ?
PK[table1]:SSN,Phone
DOUBLE_INC2[table1,table2]:SSN,SSN
DOUBLE_INC3[table3,table4]:SSN,SSN
DOUBLE_INC4[table1,table4]:SSN,SSN (inherited)

mappings
table1 : select SSN,Phone from table0
table3 : select SSN,Name from table0
table4 : select SSN,DepName,DepAdd from table0

reverse mappings
table0 : select * from table4
   join table1 on table4.SSN=table1.SSN
   join table3 on table4.SSN=table3.SSN

 */
class CmdParserDemo1_2Test: TraceParserTest() {

    /*
        table1: SSN,Phone
        table2: SSN,Name,DepName,DepAddress
        FD[table2]:SSN-->Name
        FD[table2]:SSN-->DepName
        PK[table1]:SSN,Phone
        DOUBLE_INC[table2,table2]:DepName,DepAddress
        DOUBLE_INC2[table1,table2]:SSN,SSN
     */
    internal fun demo1_2_given(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-table table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-table table2:SSN,Name,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-functional table2:SSN-->Name", context).first)
        checkOk(parser.parse("add-functional table2:SSN-->DepName", context).first)
        checkOk(parser.parse("add-key table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-double-inc table2:DepName<->table2:DepAddress", context).first)
        checkOk(parser.parse("add-double-inc table1:SSN<->table2:SSN", context).first)
        checkOk(parser.parse("add-mapping table1 select SSN,Phone from table0", context).first)
        checkOk(parser.parse("add-mapping table2 select SSN,Name,DepName,DepAddress from table0", context).first)
    }

    @Test
    fun test_changeset_demo1_2_given() {
       // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_2_given(parser, context)
        // then
        assertEquals(2, context.env.database.schema.tables?.size)
        assertEquals(5, context.env.database.schema.constraints?.size)
        assertEquals(2, context.env.database.schema.functionals().size)
        assertEquals(2, context.env.database.schema.constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size)
        assertEquals(0, context.env.database.schema.constraintsByType(Constraint.TYPE.MULTIVALUED).size)
    }

    /*
        changeset
        add table3:SSN,Name
        add table4:SSN,DepName,DepAdd
        add PK2[table3]:SSN
        add PK3[table4]:SSN  (inherited)
        add DOUBLE_INC3[table3,table4]:SSN,SSN
        add DOUBLE_INC4[table1,table4]:SSN,SSN (inherited)
        remove table2
        remove FD:SSN-->Dep (override by PK3)
     */
    internal fun demo1_2_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-cs-table table3:SSN,Name", context).first)
        checkOk(parser.parse("add-cs-table table4:SSN,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-cs-key table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-cs-key table3:SSN", context).first)
        checkOk(parser.parse("add-cs-key table4:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table1:SSN<->table4:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table1:SSN<->table3:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table3:SSN<->table4:SSN", context).first)
        checkOk(parser.parse("add-cs-double-inc table4:DepName<->table4:DepAddress", context).first)

        checkOk(parser.parse("del-cs-table table2", context).first)
        checkOk(parser.parse("del-cs-mapping table2", context).first)

        checkOk(parser.parse("add-cs-mapping table3 select SSN,Name from table2", context).first)
        checkOk(parser.parse("add-cs-mapping table4 select SSN,DepName,DepAddress from table2", context).first)
    }

    @Test
    fun test_changeset_demo1_2_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_2_given(parser,context)
        assertEquals(0, context.env.changeSet.size())
        // when
        demo1_2_when(parser, context)
        // then
        assertEquals(13 + 1, context.env.changeSet.size())// 13+1 drop table => drop constraints
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
        table4:SSN,DepName,DepAdd
        PK2[table3]:SSN
        PK3[table4]:SSN ?
        PK[table1]:SSN,Phone
        DOUBLE_INC2[table1,table2]:SSN,SSN
        DOUBLE_INC3[table3,table4]:SSN,SSN
        DOUBLE_INC4[table1,table4]:SSN,SSN (inherited)

        mappings
        table1 : select SSN,Phone from table0
        table3 : select SSN,Name from table0
        table4 : select SSN,DepName,DepAdd from table0
     */
    fun test_changeset_demo1_2_then() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_2_given(parser,context)
        demo1_2_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNull(context.env.database.schema.table("table0"))
        assertNull(context.env.database.schema.table("table2"))
        assertNotNull(context.env.database.schema.table("table1"))
        assertNotNull(context.env.database.schema.table("table3"))
        assertNotNull(context.env.database.schema.table("table4"))

        assertNull(context.env.database.mapping("table2"))
        assertNotNull(context.env.database.mapping("table1"))
        assertNotNull(context.env.database.mapping("table3"))
        assertNotNull(context.env.database.mapping("table4"))
    }

    @Test
    fun test_changeset_demo1_2_chageset_sql() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_2_given(parser,context)
        demo1_2_when(parser,context)
        // when
        val sql = SQLizeCreateUseCase().createTableMappings(context.env.changeSet)
        // then
        assertEquals(9, sql.size)
        assertEquals("CREATE TABLE public.table3 AS\n" +
                "SELECT SSN,Name\n" +
                "FROM   table2", sql[0])
        assertEquals("CREATE TABLE public.table4 AS\n" +
                "SELECT SSN,DepName,DepAddress\n" +
                "FROM   table2" , sql[1])
        assertEquals("ALTER TABLE table1 ALTER COLUMN SSN Varchar NOT NULL", sql[2])
        assertEquals("ALTER TABLE table1 ALTER COLUMN Phone Varchar NOT NULL", sql[3])
        assertEquals("ALTER TABLE table1 ADD PRIMARY KEY (SSN,Phone)", sql[4])
        assertEquals("ALTER TABLE table3 ALTER COLUMN SSN Varchar NOT NULL", sql[5])
        assertEquals("ALTER TABLE table3 ADD PRIMARY KEY (SSN)", sql[6])
        assertEquals("ALTER TABLE table4 ALTER COLUMN SSN Varchar NOT NULL", sql[7])
        assertEquals("ALTER TABLE table4 ADD PRIMARY KEY (SSN)", sql[8])
    }

}