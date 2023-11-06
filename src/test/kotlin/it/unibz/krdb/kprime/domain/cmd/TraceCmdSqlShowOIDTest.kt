package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShow6NF
import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShowOID
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertEquals

class TraceCmdSqlShowOIDTest {

    @Test
    fun test_one_table_one_primary_key_some_attribute() {
        // given
        val db = Database()
        db.schema.addTable("person:name,address,city,zip")
        db.schema.addKey("person:name")
        // when
        val result = TraceCmdSqlShowOID.computeOID(db,"person")
        // then
        assertEquals("""
            -- *
            -- * TABLE person OID
            -- *                            
            CREATE TABLE personOID AS SELECT name FROM person WHERE name IS NOT NULL;
            
            ALTER TABLE personOID ADD COLUMN OID int PRIMARY KEY AUTO_INCREMENT;
             
            -- * TABLE person_OID_address
            
            CREATE TABLE person_OID_address AS 
                SELECT di.OID ,d.address
                    FROM person d LEFT JOIN personOID di 
                    ON d.name = di.name
                    WHERE d.address IS NOT NULL ;
            
            ALTER TABLE person_OID_address ADD PRIMARY KEY(OID);
            
                                                
            -- * TABLE person_OID_city
            
            CREATE TABLE person_OID_city AS 
                SELECT di.OID ,d.city
                    FROM person d LEFT JOIN personOID di 
                    ON d.name = di.name
                    WHERE d.city IS NOT NULL ;
            
            ALTER TABLE person_OID_city ADD PRIMARY KEY(OID);
            
                                                
            -- * TABLE person_OID_zip
            
            CREATE TABLE person_OID_zip AS 
                SELECT di.OID ,d.zip
                    FROM person d LEFT JOIN personOID di 
                    ON d.name = di.name
                    WHERE d.zip IS NOT NULL ;
            
            ALTER TABLE person_OID_zip ADD PRIMARY KEY(OID);
            
                                                
            -- * TABLE person cross references integrity constraints
            
            ALTER TABLE person_OID_address ADD FOREIGN KEY (OID)
            REFERENCES person_OID_city(OID);
            
            ALTER TABLE person_OID_city ADD FOREIGN KEY (OID)
            REFERENCES person_OID_address(OID);
            
            ALTER TABLE person_OID_city ADD FOREIGN KEY (OID)
            REFERENCES person_OID_zip(OID);
            
            ALTER TABLE person_OID_zip ADD FOREIGN KEY (OID)
            REFERENCES person_OID_city(OID);
            
            ALTER TABLE person_OID_zip ADD FOREIGN KEY (OID)
            REFERENCES person_OID_address(OID);
            
            ALTER TABLE person_OID_address ADD FOREIGN KEY (OID)
            REFERENCES person_OID_zip(OID);

        """.trimIndent(),result)
    }
}