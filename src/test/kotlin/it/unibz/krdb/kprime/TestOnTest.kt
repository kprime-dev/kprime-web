package it.unibz.krdb.kprime

import org.junit.Test
import kotlin.test.assertTrue

class TestOnTest {

    @Test
    fun list_contains() {
        assertTrue(listOf<String>("gino", "pino").contains("pino"))
    }

}