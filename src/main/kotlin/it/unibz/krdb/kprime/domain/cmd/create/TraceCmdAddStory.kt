package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdAddStory : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-story"
    }

    override fun getCmdDescription(): String {
        return "Add one story current trace."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <story-name>"
    }

    override fun getCmdTopics(): String {
        return "write,conceptual,document"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val currentTrace = context.env.currentTrace?.value
        val tokens = command.split(" ")
        if (tokens.size!=2) return TraceCmdResult() failure "Required one argument: the name of the story."
        val storyname = tokens[1]
        if (currentTrace == null || currentTrace.isEmpty()) return TraceCmdResult() failure "no trace selected: {$currentTrace}."
        if (storyname.isEmpty()) return TraceCmdResult() failure "no story name given."
        val storyText = """
            # Story ${storyname}
            
            ## Given
            ## When
            ## Who
            ## What
            ## Then
            ## Examples
        """.trimIndent()

        val filename = "Story_${storyname}.md"
        val pathname = context.getCurrentTraceDir() + filename
        File(pathname).writeText(storyText)
        context.env.currentTraceFileName = filename
        return TraceCmdResult() message "Story_${storyname}.md created into trace ${currentTrace}."
    }

}
