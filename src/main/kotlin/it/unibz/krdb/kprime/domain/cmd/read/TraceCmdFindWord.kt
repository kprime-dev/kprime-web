package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdFindWord: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "find-word"
    }

    override fun getCmdDescription(): String {
        return "Find words in docs."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <word-to-find>"
    }

    override fun getCmdTopics(): String {
        return "read,document"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        if (tokens.size != 2) return TraceCmdResult() failure "One term argument required."
        val term = tokens[1]
        if (term.isEmpty()) return TraceCmdResult() failure "One term argument required."
        // extract terms to find
        val workingDir = context.pool.settingService.getWorkingDir()
        var dictionary = mutableMapOf<String,Int>()
        // walk each file in working dir
        val resultMessage = File(workingDir).walk()
            .filter{ it.isFile && isFileToGrep(it) }
            .map { findWords(it,term,dictionary) }
            .filter { it.isNotEmpty() }
        val dictionaryMessage = dictionary.entries.joinToString { "${it.key} : ${it.value} "+System.lineSeparator() }
        return TraceCmdResult() message "RESULT :[$resultMessage][$dictionaryMessage]"
    }

    private fun isFileToGrep(it: File) = it.name.endsWith(".md")

    private fun findWords(file: File, term: String, dictionary: MutableMap<String, Int>): String {
        val readLines = file.readLines()
        var result = "${readLines.size}"
        println(">>>>  read ${file.absolutePath} lines size : $result")
        for (line in readLines) {
            for (word in line.split(" ")) {
                val count = dictionary[word]?:0
                dictionary[word] = count+ 1
                println("$word : $count")
            }
        }
        return result
    }


}

