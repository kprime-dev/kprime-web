package it.unibz.krdb.kprime.adapter.fact

import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertEquals

class FactDescriptorTest {

    @Test
    fun test_fact_database_no_table() {
        // given
        val database = Database()
        // when
        val description = FactDescriptor().describe(database ,"")
        // then
        assertEquals("",description)
    }

    @Test
    fun test_fact_database_table_column() {
        // given
        val database = Database()
        database.schema.addTable("Person:Name,Surname")
        val col = Column.of("Phone")
        database.schema.table("Person")?.columns?.add(col)
        // when
        val description = FactDescriptor().describe(database ,"")
        // then
        assertEquals("""

            Person  of   
            has:
            Name type [Name] 
            Surname type [Surname] 
            Phone 
            
        """.trimIndent(),description)
    }

    @Test
    fun test_fact_database_table_column_with_cardinality() {
        // given
        val database = Database()
        database.schema.addTable("Person:Name,Surname")
        val col = Column.of("Phone")
        col.cardinality= "M_N"
        database.schema.table("Person")?.columns?.add(col)
        // when
        val description = FactDescriptor().describe(database ,"")
        // then
        assertEquals("""
            
            Person  of   
            has:
            Name type [Name] 
            Surname type [Surname] 
            Phone card [M_N] 
            
        """.trimIndent(),description)
    }

}