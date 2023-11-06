package it.unibz.krdb.kprime.domain.cmd.argument

import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.URL_PATTERN
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class TraceCmdArgumentURLTest {

    @Test
    fun test_URL_regexp() {
        val regex = URL_PATTERN.toRegex()
        assertTrue(regex.matches("https://wwwwWW9EW.wwwwww/qa/werwer/sdfsdf.sdfs"))
        assertFalse(regex.matches("sdfsdfsdfs"))
    }

    @Test
    fun test_valid_url() {
        // given
        val validator = TraceCmdArgumentURL("source_url","Required source URL.")
        // when
        val validationErrors = validator.computeValidationErrors("https://www.google.com")
        // then
        assertEquals(0,validationErrors.size)
    }

    @Test
    fun test_valid_namespace() {
        // given
        val validator = TraceCmdArgumentURL("source_url","Required source URL.")
        // when
        val validationErrors = validator.computeValidationErrors("http://www.localhost.com:8080/kprime_case_kprime")
        // then
        assertEquals(0,validationErrors.size)
    }

    @Test
    fun test_invalid_url() {
        // given
        val validator = TraceCmdArgumentURL("source_url","Required source URL.")
        // when
        val validationErrors = validator.computeValidationErrors("httgoogle")
        // then
        assertEquals(1,validationErrors.size)
        assertEquals("source_url[httgoogle] has forbidden chars, required chars in pattern [https?://[a-zA-Z0-9-./_:]*\$] .",validationErrors[0])
    }

    @Test
    fun test_invalid_url2() {
        // given
        val validator = TraceCmdArgumentURL("source_url","Required source URL.")
        // when
        val validationErrors = validator.computeValidationErrors("https://google$")
        // then
        assertEquals(1,validationErrors.size)
        assertEquals("source_url[https://google\$] has forbidden chars, required chars in pattern [https?://[a-zA-Z0-9-./_:]*\$] .",validationErrors[0])
    }

}