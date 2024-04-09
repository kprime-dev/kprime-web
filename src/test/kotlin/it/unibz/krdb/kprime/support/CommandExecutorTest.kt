package it.unibz.krdb.kprime.support

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandExecutorTest {

    @Test
    @Ignore
    fun test_system_command_execution() {
        // given
        val executor = CommandExecutor()
        // when
        val executionResult = executor.executeProcess("ls")
        // then
        assertEquals("""
            dependency-reduced-pom.xml
            doc
            Dockerfile
            env-file.properties
            kp.ipynb
            kprime
            kprime-wa.iml
            kprime-webapp
            LICENSE
            Makefile
            package-lock.json
            pom.xml
            README.md
            reflection.json
            release
            src
            target
            
        """.trimIndent(),executionResult)
    }
}