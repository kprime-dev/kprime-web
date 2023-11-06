package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShow6NF
import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShow6NFSelect
import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShowOID
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertEquals

class TraceCmdSqlShow6NFSelectTest {

    @Test
    fun test_one_table_one_primary_key_some_attribute() {
        // given
        val db = Database()
        db.schema.addTable("person:name,address,city,zip")
        db.schema.addKey("person:name")
        // when
        val result = TraceCmdSqlShow6NFSelect.compute6NF(db,"person")
        // then
        assertEquals("""
            -- *
            -- * TABLE person
            -- *
            CREATE VIEW person_6NF AS 
                SELECT 
person_6NF_id_address.name,
person_6NF_id_address.address,
person_6NF_id_city.city,
person_6NF_id_zip.zip
                FROM 
person_6NF_id_address,
person_6NF_id_city,
person_6NF_id_zip
                WHERE  
       person_6NF_id_address.name = person_6NF_id_city.name AND 
       person_6NF_id_city.name = person_6NF_id_zip.name AND 
       person_6NF_id_zip.name = person_6NF_id_address.name
               """.trimIndent(),result)
    }
}