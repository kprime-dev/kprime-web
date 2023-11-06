package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdFindParagraphs: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "find-paragraph"
    }

    override fun getCmdDescription(): String {
        return "Find paragraph in docs containing a term in headline."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <term-to-find>"
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
        // walk each file in working dir
        val resultMessage = File(workingDir).walk().filter{
            it -> it.isFile && isFileToGrep(it)
        }.map {
            it -> findParagraph(it,term)
        }.filter { it -> it.isNotEmpty() }
        return TraceCmdResult() message resultMessage.joinToString(System.lineSeparator())
    }

    private fun isFileToGrep(it: File) = it.name.endsWith(".md")

    private fun findParagraph(file:File, term:String): String {
        var result = ""
        val readLines = file.readLines()
        var inResult = false
        var isNewFile = true
        for (line in readLines) {
            if (line.startsWith("#") && line.contains(term)) {
                inResult = true
            }
            if (line.startsWith("#") && !line.contains(term)) {
                inResult = false
            }
            if (inResult) {
                if (isNewFile) {
                    result += " ============ ${file.name} "
                    isNewFile = false
                }
                result += line + System.lineSeparator()
            }
        }
        return result
    }


}

