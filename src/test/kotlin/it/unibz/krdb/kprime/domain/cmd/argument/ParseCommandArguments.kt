package it.unibz.krdb.kprime.domain.cmd.argument

import org.junit.Test

class ParseCommandArguments {

    @Test
    fun test_parse_cmd_args() {
        // given
        val cmd = "log -file1 +file2 file3 +file_4 file5 file 6"
        // when
        val args = cmd.split(" ")
        val requiredArgs = args.filterIndexed { index, arg -> index > 0 && arg[0] != '+' }
        val optionalArgs = args.filterIndexed { index, arg -> index > 0 && arg[0] == '+' }
        // then
        println("Required arguments: $requiredArgs")
        println("Optional arguments: $optionalArgs")
    }
}