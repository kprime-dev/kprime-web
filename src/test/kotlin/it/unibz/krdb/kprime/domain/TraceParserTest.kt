package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import kotlin.test.assertTrue

abstract class TraceParserTest {

    protected fun checkOk(result: TraceCmdResult) {
        if (result.failure.isNotEmpty()) {
            System.err.println(result.failure)
        }
        assertTrue(result.isOK())
    }

}