package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdFindText: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "find-text"
    }

    override fun getCmdDescription(): String {
        return "Find text in docs using lucene syntax."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [-updated] <text-to-find>"
    }

    override fun getCmdTopics(): String {
        return "read,document"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        if (tokens.size < 2) return TraceCmdResult() failure "One term argument required, text to find."
        val subcommand = tokens[1]
        val updated = subcommand == "-updated"
        val termQuery =  if (updated)
            tokens.drop(2).joinToString(" ")
        else
            tokens.drop(1).joinToString(" ")
        if (termQuery.isEmpty()) return TraceCmdResult() failure "One term argument required, text to find."
        // extract terms to find
        val workingDir = context.pool.settingService.getWorkingDir()

        context.pool.storyService.indexAllStories(updated)

        // search for text in index
        val resultMessage = context.pool.storyService.findTextAsString(workingDir,
            context.pool.storyService.findText(workingDir,termQuery))

        return TraceCmdResult() message resultMessage
    }


}

