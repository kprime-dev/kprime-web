package it.unibz.krdb.kprime.generic.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.Test

class JacksonLocalDateTime {

    @Test
    fun test_from_string() {
        // given
        val datetime = ""
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        // when

    }
}