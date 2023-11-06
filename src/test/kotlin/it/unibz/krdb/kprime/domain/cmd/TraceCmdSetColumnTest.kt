package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.ContextMocker
import it.unibz.krdb.kprime.domain.cmd.update.TraceCmdSetColumn
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser
import kotlin.test.assertEquals

class TraceCmdSetColumnTest {

    @Test
    fun test_set_nullable_columns() {
        // given
        val table = SchemaCmdParser.parseTable("person:name,surname")
        val context = ContextMocker.withTable(table)
        // when
        val result = TraceCmdSetColumn.execute(context,
                "set-column person:name nullable")
        // then
        val updatedTable = context.env.database.schema.table("person")
        assertEquals(true, updatedTable?.colByName("name")?.nullable)
        assertEquals(false, updatedTable?.colByName("surname")?.nullable)
    }

    @Test
    fun test_set_multiple_nullable_columns() {
        // given
        val table = SchemaCmdParser.parseTable("person:name,surname,age")
        val context = ContextMocker.withTable(table)
        // when
        val result = TraceCmdSetColumn.execute(context,
                "set-column person:name,age nullable")
        // then
        val updatedTable = context.env.database.schema.table("person")
        assertEquals(true, updatedTable?.colByName("name")?.nullable)
        assertEquals(false, updatedTable?.colByName("surname")?.nullable)
        assertEquals(true, updatedTable?.colByName("age")?.nullable)
    }

    @Test
    fun test_set_multiple_not_nullable_columns() {
        // given
        val table = SchemaCmdParser.parseTable("person:name,surname,age")
        val context = ContextMocker.withTable(table)
        TraceCmdSetColumn.execute(context,
                "set-column person:name,age nullable")
        // when
        TraceCmdSetColumn.execute(context,
                "set-column person:name,age not-nullable")
        // then
        val updatedTable = context.env.database.schema.table("person")
        assertEquals(false, updatedTable?.colByName("name")?.nullable)
        assertEquals(false, updatedTable?.colByName("surname")?.nullable)
        assertEquals(false, updatedTable?.colByName("age")?.nullable)
    }
}