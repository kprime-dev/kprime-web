package it.unibz.krdb.kprime.generic.kotlin

import it.unibz.krdb.kprime.support.*
import org.junit.Test
import kotlin.test.assertEquals

class StringTest {

    @Test
    fun test_linearize_string() {
        //given
        val text = "match\n provaci \n ancora;"
        //when
        val newtext = text.split(System.lineSeparator()).joinToString("")
        // then
        assertEquals("match provaci  ancora;",newtext)
    }

    @Test
    fun test_alfanumeric() {
        // given
        val text = "qwewer 2342rwer%£%\"£$%£\"&_wserw"
        // then
        assertEquals("qwewer2342rwerwserw",text.alfaNumeric())
    }

    @Test
    fun test_alfanumeric_with_alternative() {
        // given
        val text = "qwewer 2342rwer%£%\"£$%£\"&_wserw"
        // then
        assertEquals("qwewer_2342rwer___________wserw",text.alfaNumeric("_"))
    }

    @Test
    fun test_substring() {
        // given
        val text = "fromstartthereis texttoan end."
        // then
        assertEquals("thereis texttoan ",text.substring("start","end"))
    }

    @Test
    fun test_extraString() {
        // given
        val text = "fromstartthereis texttoan end..."
        // then
        assertEquals("from...",text.extraString("start","end"))
    }

    @Test
    fun test_substring_before() {
        // given
        val text = "fromstartthereis:texttoan end."
        // then
        assertEquals("fromstartthereis",text.substringBefore(":"))
        // given
        val text2 = "fromstartthereis"
        // then
        assertEquals("fromstartthereis",text2.substringBefore(":"))
        // given
        val text3 = "[result:[1]<p>Ok. 13 terms.<br></p>]]"
        // then
        assertEquals("[1]<p>Ok. 13 terms.<br></p>",
            text3.substring("[result:","]]"))
    }

    @Test
    fun test_camelToSnakeCase() {
        // given
        val camelCaseInput1 = "myNiceCamelCaseString"
        val camelCaseInput2 = "MyNiceCamelCaseString"
        val expectedInSnakeCase = "my_nice_camel_case_string"
        // then
        assertEquals(expectedInSnakeCase, camelCaseInput1.camelToSnakeCase())
        assertEquals(expectedInSnakeCase, camelCaseInput2.camelToSnakeCase())
    }

    @Test
    fun test_snakeToCamelCase() {
        // given
        val snakeCaseInput = "one_good_snake_case_string"
        val expectedInCamelCase = "oneGoodSnakeCaseString"
        // then
        assertEquals(expectedInCamelCase, snakeCaseInput.snakeToCamelCase())
    }

    @Test
    fun test_dashToCamelCase() {
        // given
        val dashCaseInput = "one-good-snake-case-string"
        val expectedInCamelCase = "oneGoodSnakeCaseString"
        // then
        assertEquals(expectedInCamelCase, dashCaseInput.dashToCamelCase())
    }

    @Test
    fun test_noblank() {
        // given
        val blankCaseInput = "one Good-Snake Case String"
        val blankCaseInput2 = "Event Plan Category"
        // then
        assertEquals("oneGoodSnakeCaseString", blankCaseInput.dashToCamelCase().noBlank())
        assertEquals("EventPlanCategory", blankCaseInput2.noBlank())
    }

    @Test
    fun test_afterLast() {
        assertEquals("dame","ciao/dame".substringAfterLast("/"))
        assertEquals("dame","dame".substringAfterLast("/"))
    }

    @Test
    fun test_substring_qname() {
        assertEquals("string","\"string\"@en".substring("\"","\""))
    }

    @Test
    fun unwrapping_string() {
        assertEquals("abc", "'abc'".drop(1).dropLast(1))
    }
}