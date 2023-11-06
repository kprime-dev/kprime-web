package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test

class KotlinMapTest {

    @Test fun test_for_null_values() {
        // given
        val map = HashMap<String,Any?>()
        // when
        map["abc"] = null
    }
}