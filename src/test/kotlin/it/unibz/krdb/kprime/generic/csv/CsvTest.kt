package it.unibz.krdb.kprime.generic.csv

import org.apache.commons.csv.CSVFormat
import org.junit.Test
import java.io.InputStreamReader
import kotlin.test.assertEquals

class CsvTest {

    @Test
    fun test_countries_csv_read() {
        // given
        val input = this.javaClass.getResourceAsStream("/csv/countries.csv")
        // when
        val parsed = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(InputStreamReader(input))
        // then
        assertEquals("{name=0, alpha-2=1, alpha-3=2, country-code=3, iso_3166-2=4, region=5, sub-region=6, intermediate-region=7, region-code=8, sub-region-code=9, intermediate-region-code=10}", parsed.headerMap.toString())
        assertEquals(0, parsed.recordNumber)
        // record loop is required to get real parsed recordNumber.
        for (record in parsed.records) {
            //println(record.get(1))
        }
        assertEquals(249L, parsed.recordNumber)

    }
}