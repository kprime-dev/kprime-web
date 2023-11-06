package it.unibz.krdb.kprime.adapter.chart

import org.junit.Test
import kotlin.test.assertEquals

class ChartLabelsServiceTest {

    @Test
    fun test_chartSafeName() {
        // given
        val name = "http://purl.org/dc/terms/created"
        // then
        assertEquals("http__purl_org_dc_terms_created",ChartLabelsService.chartSafeName(name))
    }

    @Test
    fun test_chartSafeName_text() {
        // given
        val name = "Alfa Beta"
        // then
        assertEquals("Alfa_Beta",ChartLabelsService.chartSafeName(name))
    }

    @Test
    fun test_chartSafeName_comment() {
        // given
        val comment = "\"Represents a collection of observations possibly organized into various slices conforming to some common dimensional structure.\"@en"
        // then
        assertEquals("Represents_a_collection_of_observations_possibly_",ChartLabelsService.chartSafeName(comment))
    }
}