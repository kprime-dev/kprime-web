package it.unibz.krdb.kprime.domain.cmd.argument

import org.junit.Test
import kotlin.test.assertEquals

class TraceCmdArgumentQNameTest {

    @Test
    fun test_path_syntax_validation() {
        // given
        val validator = TraceCmdArgumentQName("myqname","Required qname.")
        // when
        val validationErrors = validator.computeValidationErrors("Person")
        // then
        for (error in validationErrors) {
            System.out.println(error)
        }
        assertEquals(0,validationErrors.size)
    }

}