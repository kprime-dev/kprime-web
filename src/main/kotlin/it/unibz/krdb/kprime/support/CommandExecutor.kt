package it.unibz.krdb.kprime.support

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Execute a Command on host operating system .
 */
class CommandExecutor  {

    fun executeProcess(command:String):String {
        val process = Runtime.getRuntime().exec(command)
        val inputStream = process.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))
        val output = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            output.append(line).append("\n")
        }
        return output.toString()
    }

}

