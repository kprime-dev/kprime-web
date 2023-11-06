package it.unibz.krdb.kprime.domain.cmd.argument

import junit.framework.TestCase.assertNotNull
import org.junit.Test
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals


class TraceCmdArgumentHourTest {

    @Test
    fun test_parse_format_hour() {
        // given
        val argDate = TraceCmdArgumentHour("","")
        // when
        val date = argDate.fromString("16:23:01")
        // then
        assertEquals("16:23:01",argDate.asString(date))
    }

    @Test
    fun test_validate_hour() {
        // given
        val argDate = TraceCmdArgumentHour("","")
        // when
        val errors = argDate.computeValidationErrors("0702-2023")
        // then
        assertEquals(1,errors.size)
        assertEquals(" requires an hour in format hh:mm:ss.",errors[0])
    }

    @Test
    fun test_local_date_time() {
        val timeString = "12:00:00"
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localDateTime = LocalTime.parse(timeString,formatter)
        val instant: Instant = localDateTime.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC)
        val date = Date.from(instant)
        assertNotNull(date)
    }
}