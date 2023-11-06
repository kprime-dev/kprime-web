package it.unibz.krdb.kprime.domain.cmd.argument

import org.junit.Test
import kotlin.test.assertEquals

class TraceCmdArgumentFilePathTest {

    @Test
    fun test_path_syntax_validation() {
        // given
        val validator = TraceCmdArgumentFilePath("filepath","Required file path.")
        // when
        val validationErrors = validator.computeValidationErrors("stories/facts.md")
        // then
        for (error in validationErrors) {
            System.out.println(error)
        }
        assertEquals(0,validationErrors.size)

    }
}