package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlSelect
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class TraceCmdSqlSelectTest {

    @Test
    fun test_empty_sql_form_parse() {
        // given
        val cmd = TraceCmdSqlSelect
        // when
        val (sql, source) = cmd.extractDatasourceNameFromCmd("")
        // then
        assertEquals("",sql)
        assertEquals("",source)
    }

    @Test
    fun test_sql_form_parse() {
        // given
        val cmd = TraceCmdSqlSelect
        // when
        val (sql, source) = cmd.extractDatasourceNameFromCmd("select * from persone:persona")
        // then
        assertEquals("select * from persona",sql)
        assertEquals("persone",source)
    }

    @Test
    fun test_simple_sql_form_parse() {
        // given
        val cmd = TraceCmdSqlSelect
        // when
        val (sql, source) = cmd.extractDatasourceNameFromCmd("select * from persona")
        // then
        assertEquals("select * from persona",sql)
        assertEquals("",source)
    }

    @Test
    fun test_where_sql_form_parse() {
        // given
        val cmd = TraceCmdSqlSelect
        // when
        val (sql, source) = cmd.extractDatasourceNameFromCmd("select * from persone:persona where a=b")
        // then
        assertEquals("select * from persona where a=b",sql)
        assertEquals("persone",source)
    }

    @Test
    fun test_sqlCommandViaEnvelope() {
        // given
        val sqlCommand = "XYZ"
        val cmdEnvelope = CmdEnvelope(emptyList(),"", listOf("SELECT * FROM Envelope;"))
        // when
        val sqlCommandViaEnvelope = TraceCmdSqlSelect.sqlCommandViaEnvelope(cmdEnvelope, sqlCommand)
        // then
        assertEquals("SELECT SELECT * FROM Envelope;",sqlCommandViaEnvelope)
    }

    @Test
    fun test_sqlCommandViaMappingName() {
        // given
        val sqlCommand = "SELECT myquery"
        val database = Database()
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("myquery","SELECT * FROM Mappings;")))
        // when
        val sqlCommandViaMappingName = TraceCmdSqlSelect.sqlCommandViaMappingName(database, sqlCommand)
        // then
        assertEquals("""
            SELECT *
            FROM   Mappings; 
         """.trimIndent(),sqlCommandViaMappingName)
    }

    @Test
    fun test_sqlCommandViaMappingNameExploded() {
        // given
        val sqlCommand = "SELECT myquery"
        val database = Database()
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("mymap","SELECT Surname,Name FROM table0")))
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("myquery","SELECT * FROM mymap")))
        // when
        val sqlCommandViaMappingName = TraceCmdSqlSelect.sqlCommandViaMappingName(database, sqlCommand)
        // then
        assertEquals("""
            SELECT Surname,Name
            FROM   table0 
         """.trimIndent(),sqlCommandViaMappingName)
    }

    @Test
    fun test_sqlCommandViaMappingNameExplodedIntersectAttributes() {
        // given
        val sqlCommand = "SELECT myquery"
        val database = Database()
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("mymap","select SSN,Name,Phone from table0 where a=B")))
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("myquery","SELECT Phone FROM mymap where c=D")))
        // when
        val sqlCommandViaMappingName = TraceCmdSqlSelect.sqlCommandViaMappingName(database, sqlCommand)
        // then
        assertEquals("""
            SELECT Phone
            FROM   table0
            WHERE c=D AND a=B 
         """.trimIndent(),sqlCommandViaMappingName)
    }


    @Test
    fun test_sqlCommandViaMappingNameExplodedThreeMapping() {
        // given
        val sqlCommand = "SELECT myquery"
        val database = Database()
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("root","select SSN,Name from table0 where x=Z")))
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("mymap","select Name,Phone from root where a=B")))
        database.mappings().add(Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("myquery","SELECT * FROM mymap where c=D")))
        // when
        val sqlCommandViaMappingName = TraceCmdSqlSelect.sqlCommandViaMappingName(database, sqlCommand)
        // then
        assertEquals("""
            SELECT Name
            FROM   table0
            WHERE c=D AND a=B AND x=Z 
         """.trimIndent(),sqlCommandViaMappingName)
    }

    @Test
    fun test_sql() {
        // given
        val sqlCommand = "SELECT * FROM Default"
        // when
        val sqlCommandWithDefaultLimit = TraceCmdSqlSelect.sqlCommandWithDefaultLimit(sqlCommand)
        // then
        assertEquals("SELECT * FROM Default LIMIT 10",sqlCommandWithDefaultLimit)
    }
}