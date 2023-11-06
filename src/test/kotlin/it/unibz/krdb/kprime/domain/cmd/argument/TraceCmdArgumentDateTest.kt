package it.unibz.krdb.kprime.domain.cmd.argument

import org.junit.Test
import kotlin.test.assertEquals

class TraceCmdArgumentDateTest {

    @Test
    fun test_parse_format_date() {
        // given
        val argDate = TraceCmdArgumentDate("","")
        // when
        val date = argDate.fromString("07-02-2023")
        // then
        assertEquals("07-02-2023",argDate.asString(date))
    }

    @Test
    fun test_validate_date() {
        // given
        val argDate = TraceCmdArgumentDate("","")
        // when
        val errors = argDate.computeValidationErrors("0702-2023")
        // then
        assertEquals(1,errors.size)
        assertEquals(" requires a date in format dd-mm-yyyy.",errors[0])
    }

}