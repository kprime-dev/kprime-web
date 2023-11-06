package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.ContextMocker
import org.junit.Test
import kotlin.test.assertEquals
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddTable
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddFact

class TraceCmdAddFactTest {


    /**
     * problems:
     *      1. name of key is always equals.
     *      2. there is no person table, but no error is thrown.
     */
    @Test
    fun test_add_key() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        assertEquals(0, context.env.database.schema.keys().size)
        TraceCmdAddTable.execute(context, "add-table person:name,surname")
        println(context.env.database.schema.table("person"))
        // when
        val cmd = TraceCmdAddFact().execute(
                context, "add-fact person has-key name")
        //then
        assertEquals(1, context.env.database.schema.keys().size)
        assertEquals("KEY_person_name", context.env.database.schema.keys()[0].name)
    }

    @Test
    fun test_double_add_key() {
        // given
        val context = ContextMocker.withEmptyDatabase()
        TraceCmdAddTable.execute(context, "add-table person:name,surname")
        // when
        val cmd1 = TraceCmdAddFact().execute(
                context, "add-fact person has-key name")
        val cmd2 = TraceCmdAddFact().execute(
                context, "add-fact person has-key surname")
        //then
        assertEquals(2, context.env.database.schema.keysAll().size)
        assertEquals(1, context.env.database.schema.keys().size)
        assertEquals("KEY_person_surname", context.env.database.schema.keys()[0].name)
        val keysAll = context.env.database.schema.keysAll()
        println(keysAll)
        assertEquals("KEY_person_name", keysAll[0].name)
        assertEquals("CANDIDATE_KEY", keysAll[0].type)
        assertEquals("KEY_person_surname", keysAll[1].name)
        assertEquals("PRIMARY_KEY", keysAll[1].type)
    }

    @Test
    fun test_is_a_partition() {
        // given
        val context = ContextMocker.withEmptyDatabase();
        assertEquals(0,context.env.database.schema.tables().size)
        TraceCmdAddTable.execute(context, "add-table Plan:planId,name")
        TraceCmdAddTable.execute(context, "add-table Resource:resourceId")
        TraceCmdAddTable.execute(context, "add-table Goal:goalId")
        // when
        val cmd = TraceCmdAddFact().execute(
                context, "add-fact Why is-partitioned Plan Resource Goal")
        // then
        assertEquals(true, cmd.failure.isEmpty())
        assertEquals("Partition added.",cmd.message)
        assertEquals(true, cmd.isOK())
        assertEquals(4,context.env.database.schema.tables().size)
        assertEquals("Table(name='Why', id='', view='', condition='', parent=null, columns=[Why], catalog=null, schema=null, source=null)",
                context.env.database.schema.table("Why").toString())
        assertEquals("Table(name='Plan', id='t1', view='', condition='PARTITION', parent=Why, columns=[planId, name], catalog=null, schema=null, source=null)",
                context.env.database.schema.table("Plan").toString())
    }

}