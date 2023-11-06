package it.unibz.krdb.kprime.domain.cmd.argument

import org.junit.Test
import kotlin.test.assertEquals

class TraceCmdArgumentPathTest {

    @Test
    fun test_path_syntax_validation() {
        // given
        val validator = TraceCmdArgumentPath("filepath","Required file path.")
        // when
        val validationErrors = validator.computeValidationErrors("/aMd123/1212")
        // then
        for (error in validationErrors) {
            System.out.println(error)
        }
        assertEquals(0,validationErrors.size)
    }

}