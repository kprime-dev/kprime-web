package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File
import java.nio.charset.Charset

object TraceCmdFindTerms: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "find-term"
    }

    override fun getCmdDescription(): String {
        return "Find position in project containing a term."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <term-to-find>"
    }

    override fun getCmdTopics(): String {
        return "read,term"
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
        val resultMessage = context.pool.searchService.findTerm(term)
                .joinToString(System.lineSeparator()) { it.position }
        return TraceCmdResult() message resultMessage
    }

    private fun isFileToGrep(it: File) = it.name.endsWith(".md") ||
            it.name.endsWith(".db") ||
            it.name.endsWith(".cs")

    private fun findText(file:File, text:String): String {
        val text = file.readText(Charset.defaultCharset())
        val pattern = text.toRegex()
        val matches = pattern.findAll(text)
        var result = ""
        matches.forEach { f ->
            result += file.absolutePath+ " " + file.name + System.lineSeparator()
            val m = f.value
            val idx = f.range
            result += "$m found at indexes: $idx" + System.lineSeparator()
        }
        return result
    }

}

