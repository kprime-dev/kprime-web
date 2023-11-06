package it.unibz.krdb.kprime.support

import org.junit.Test
import kotlin.test.assertEquals

class TimeSupportTest {

    @Test
    fun test_distance() {
        // given
        val date20230801 = TimeSupport.toDate("01-01-2021")
        val date20230806 = TimeSupport.toDate("06-12-2023")
        // when
        val distance1 = TimeSupport.distance(date20230801, date20230806)
        val distance2 = TimeSupport.distance(date20230806, date20230801)
        // then
        assertEquals(5,distance1.days)
        assertEquals(11,distance1.months)
        assertEquals(2,distance1.years)

        assertEquals(-5,distance2.days)
        assertEquals(-11,distance2.months)
        assertEquals(-2,distance2.years)
    }
}