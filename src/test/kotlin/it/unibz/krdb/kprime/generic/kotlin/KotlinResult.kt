package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test
import kotlin.test.assertEquals

class KotlinResult {

    @Test
    fun test_result_success() {
        //given
        val result = Result.success("prova")
        // then
        // assert
        assertEquals(5, result.map { it.length }.getOrDefault(0))
    }
}