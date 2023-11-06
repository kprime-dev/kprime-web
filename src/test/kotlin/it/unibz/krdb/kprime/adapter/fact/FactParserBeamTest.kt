package it.unibz.krdb.kprime.adapter.fact

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import kotlin.test.assertEquals

class FactParserBeamTest {

    /**
     * BEAM Simple Fact
     */
    @Test
    fun test_beam_simple_fact() {
        // given
        val lastSubjecT = ""
        val changeSet = ChangeSet()
        val database = Database()
        val command = "add-fact Customer orders Product"
        // when
        val result = FactParser.parseFact(command,lastSubjecT,changeSet,database)
        // then
        val relation = database.schema.relation("orders")!!
        assertEquals(" orders '1' Customer 'Giovanni' orders Product 'Fruit'",
            FactPrinter.print(relation, mapOf(
                "orders" to "1",
                "Customer" to "Giovanni",
                "Product" to "Fruit"
            )))

    }

    /**
     * TODO BEAM Long Fact
     */
    @Test
    @Ignore
    fun test_beam_long_fact() {
        // given
        val lastSubjecT = ""
        val changeSet = ChangeSet()
        val database = Database()
        val command = "add-fact Customer orders Product on OrderDate from Salesperson in Quantity for Revenue"
        // when
        val result = FactParser.parseFact(command,lastSubjecT,changeSet,database)
        // then
        val relation = database.schema.relation("orders")!!
        assertEquals("  orders '1'  Customer 'Giovanni' orders Product 'Fruit'",
            FactPrinter.print(relation, mapOf(
                "orders" to "1",
                "Customer" to "Giovanni",
                "Product" to "Fruit"
            )))

    }

}