package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.sql.TraceCmdSqlShow6NF
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertEquals

class TraceCmdSqlShow6NFTest {

    @Test
    fun test_one_table_one_primary_key_some_attribute() {
        // given
        val db = Database()
        db.schema.addTable("person:name,address,city,zip")
        db.schema.addKey("person:name")
        // when
        val result = TraceCmdSqlShow6NF.compute6NF(db,"person")
        // then
        assertEquals("""
            -- *
            -- * TABLE person
            -- *
            -- * TABLE person_6NF_id_address
            
            CREATE TABLE person_6NF_id_address
            AS SELECT t.name, t.address
            FROM person t;
            
            ALTER TABLE person_6NF_id_address ADD PRIMARY KEY(name);
            
            -- * TABLE person_6NF_id_city
            
            CREATE TABLE person_6NF_id_city
            AS SELECT t.name, t.city
            FROM person t;
            
            ALTER TABLE person_6NF_id_city ADD PRIMARY KEY(name);
            
            -- * TABLE person_6NF_id_zip
            
            CREATE TABLE person_6NF_id_zip
            AS SELECT t.name, t.zip
            FROM person t;
            
            ALTER TABLE person_6NF_id_zip ADD PRIMARY KEY(name);
            
            -- * cross references integrity constraints
            
            ALTER TABLE person_6NF_id_address ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_city(name);
            
            ALTER TABLE person_6NF_id_address ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_zip(name);
            
            ALTER TABLE person_6NF_id_city ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_address(name);
            
            ALTER TABLE person_6NF_id_city ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_zip(name);
            
            ALTER TABLE person_6NF_id_zip ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_address(name);
            
            ALTER TABLE person_6NF_id_zip ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_city(name);
            
        """.trimIndent(),result)
    }

    @Test
    fun test_one_table_one_primary_key_some_attribute_one_fk() {
        // given
        val db = Database()
        db.schema.addTable("person:name,address,city,zip")
        db.schema.addTable("employee:name")
        db.schema.addKey("person:name")
        db.schema.addForeignKey("employee:name-->person:name")
        // when
        val result = TraceCmdSqlShow6NF.compute6NF(db,"person")
        // then
        assertEquals("""
            -- *
            -- * TABLE person
            -- *
            -- * TABLE person_6NF_id_address
            
            CREATE TABLE person_6NF_id_address
            AS SELECT t.name, t.address
            FROM person t;
            
            ALTER TABLE person_6NF_id_address ADD PRIMARY KEY(name);
            
            -- * TABLE person_6NF_id_city
            
            CREATE TABLE person_6NF_id_city
            AS SELECT t.name, t.city
            FROM person t;
            
            ALTER TABLE person_6NF_id_city ADD PRIMARY KEY(name);
            
            -- * TABLE person_6NF_id_zip
            
            CREATE TABLE person_6NF_id_zip
            AS SELECT t.name, t.zip
            FROM person t;
            
            ALTER TABLE person_6NF_id_zip ADD PRIMARY KEY(name);
            
            -- * cross references integrity constraints
            
            ALTER TABLE person_6NF_id_address ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_city(name);
            
            ALTER TABLE person_6NF_id_address ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_zip(name);
            
            ALTER TABLE person_6NF_id_city ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_address(name);
            
            ALTER TABLE person_6NF_id_city ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_zip(name);
            
            ALTER TABLE person_6NF_id_zip ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_address(name);
            
            ALTER TABLE person_6NF_id_zip ADD FOREIGN KEY (name)
            REFERENCES person_6NF_id_city(name);
            
            -- * updates 1 TARGET foreign key constraints 
            
            -- * updates TARGET foreign key constraints FOREIGN_KEY employee:name --> person:name ; 

        """.trimIndent(),result)
    }

}