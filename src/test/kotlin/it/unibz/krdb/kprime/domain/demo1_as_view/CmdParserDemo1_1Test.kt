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

/*
Given
table0: SSN,Phone,Name,DepName,DepAddress
MVD[table0]:SSN->>Phone
FD1[table0]:SSN-->Name
FD2[table0]:SSN-->DepName
DOUBLE_INC[table0,table0]:DepName<->DepAddess


When
trigger MVD.1:
LHS=SSN,
RHS=Phone,
REST=Name,DepName,DepAddress,
CONSTR=FD1,FD2,DOUBLE_INC
RHS'=_

vertical decomposition per la MVD SSN->>Phone

changeset
add table1:SSN,Phone
add table2:SSN,Name,DepName,DepAddress
add PK[table1]:SSN,Phone
add DOUBLE_INC2[table1,table2]:SSN<->SSN
remove table0
add mapping table1 : select SSN,Phone from table0
add mapping table2 : select SSN,Name,DepName,DepAddress from table0

Then
database
table1:SSN,Phone
table2:SSN,Name,DepName,DepAddress
PK[table1]:SSN,Phone
DOUBLE_INC2[table1,table2]:SSN<->SSN
FD1,FD2,DOUBLE_INC

mappings
table1 : select SSN,Phone from table0
table2 : select SSN,Name,DepName,DepAddress from table0

reverse mappings
table0 : select * from table1
    join table2 on table1.SSN=table2.SSN

 */
class CmdParserDemo1_1Test: TraceParserTest() {

    /*
        table0: SSN,Phone,Name,DepName,DepAddress
        MVD[table0]:SSN->>Phone
        FD1[table0]:SSN-->Name
        FD2[table0]:SSN-->DepName
        DOUBLE_INC[table0,table0]:DepName<->DepAddess
     */
    internal fun demo1_1_given(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-table table0:SSN,Phone,Name,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-multivalued table0:SSN-->Phone", context).first)
        checkOk(parser.parse("add-functional table0:SSN-->Name", context).first)
        checkOk(parser.parse("add-functional table0:SSN-->DepName", context).first)
        checkOk(parser.parse("add-double-inc table0:DepName<->table0:DepAddress", context).first)
    }

    @Test
    fun test_changeset_demo1_1_given() {
       // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        assertEquals(0, context.env.database.schema.tables?.size)
        // when
        demo1_1_given(parser, context)
        // then
        assertEquals(1, context.env.database.schema.tables?.size)
        assertEquals(4, context.env.database.schema.constraints?.size)
        assertEquals(2, context.env.database.schema.functionals().size)
        assertEquals(1, context.env.database.schema.constraintsByType(Constraint.TYPE.DOUBLE_INCLUSION).size)
        assertEquals(1, context.env.database.schema.constraintsByType(Constraint.TYPE.MULTIVALUED).size)
    }

    /*
        changeset
        add table1:SSN,Phone
        add table2:SSN,Name,DepName,DepAddress
        add PK[table1]:SSN,Phone
        add DOUBLE_INC2[table1,table2]:SSN<->SSN
        remove table0
     */
    internal fun demo1_1_when(parser: CmdParser, context: CmdContext) {
        checkOk(parser.parse("add-cs-table table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-cs-table table2:SSN,Name,DepName,DepAddress", context).first)
        checkOk(parser.parse("add-cs-key table1:SSN,Phone", context).first)
        checkOk(parser.parse("add-cs-double-inc table1:SSN<->table2:SSN", context).first)
        checkOk(parser.parse("del-cs-table table0", context).first)
        checkOk(parser.parse("add-cs-mapping table1 select SSN,Phone from table0", context).first)
        checkOk(parser.parse("add-cs-mapping table2 select SSN,Name,DepName,DepAddress from table0", context).first)
    }

    @Test
    fun test_changeset_demo1_1_when() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_1_given(parser, context)
        assertEquals(0, context.env.changeSet.size())
        // when
        demo1_1_when(parser, context)
        // then
        assertEquals(7 + 1, context.env.changeSet.size())// 7+1 drop-table => drop-constraint
        assertEquals(1, context.env.changeSet.dropTable.size)
        assertEquals(2, context.env.changeSet.createMapping.size)
    }

    @Test
    /*
        database
        table1:SSN,Phone
        table2:SSN,Name,DepName,DepAddress
        PK[table1]:SSN,Phone
        DOUBLE_INC2[table1,table2]:SSN<->SSN
        FD1,FD2,DOUBLE_INC

        mappings
        table1 : select SSN,Phone from table0
        table2 : select SSN,Name,DepName,DepAddress from table0
     */
    fun test_changeset_demo1_1_then() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_1_given(parser,context)
        demo1_1_when(parser,context)
        // when
        checkOk(parser.parse("changeset-apply",context).first)
        // then
        assertNotNull(context.env.database.schema.table("table1"))
        assertNull(context.env.database.schema.table("table0"))
        assertNotNull(context.env.database.mapping("table1"))
    }

    @Test
    fun test_changeset_demo1_1_chageset_sql() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        val parser = CmdParser()
        demo1_1_given(parser,context)
        demo1_1_when(parser,context)
        // when
        val sql = SQLizeCreateUseCase().createCommands(context.env.changeSet)
        // then
        assertEquals(7, sql.size)
        assertEquals("CREATE TABLE IF NOT EXISTS table1 (  SSN varchar(64) ,Phone varchar(64));", sql[0])
        assertEquals("CREATE TABLE IF NOT EXISTS table2 (  SSN varchar(64) ,Name varchar(64) ,DepName varchar(64) ,DepAddress varchar(64));", sql[1])
        assertEquals("CREATE OR REPLACE VIEW public.table1 AS\n" +
                "SELECT SSN,Phone\n" +
                "FROM   table0", sql[2])
        assertEquals("CREATE OR REPLACE VIEW public.table2 AS\n" +
                "SELECT SSN,Name,DepName,DepAddress\n" +
                "FROM   table0", sql[3])
        assertEquals("ALTER TABLE table1 ALTER COLUMN SSN Varchar NOT NULL", sql[4])// double-inc
        assertEquals("ALTER TABLE table1 ALTER COLUMN Phone Varchar NOT NULL", sql[5])// double-inc
        assertEquals("ALTER TABLE table1 ADD PRIMARY KEY (SSN,Phone)", sql[6])
    }

}