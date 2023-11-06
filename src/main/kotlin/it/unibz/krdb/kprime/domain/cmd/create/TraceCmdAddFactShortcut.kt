package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

class TraceCmdAddFactShortcut: TraceCmd {

    override fun getCmdName(): String {
        return "+"
    }

    override fun getCmdDescription(): String {
        return "Shortcut for add tables and facts."
    }

    override fun getCmdUsage(): String {
        return """
            + table(key):attributes
            + subj pred cobj modifiers 
        """.trimIndent()
    }

    override fun getCmdTopics(): String {
        return "write,conceptual,fact"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        println("TraceCmdAddFactShortcut command : $command")
        if (isATableDefinition(command)) return TraceCmdAddTable.execute(context,command)
        if (isAFunctionalDefinition(command)) return TraceCmdAddFunctional.execute(context,command)
        return TraceCmdAddFact().execute(context,command)
    }

    fun isATableDefinition(command: String):Boolean {
        val tokens = command.split(" ")
        return (tokens.size == 2 && (tokens[1].contains("(") || tokens[1].contains(",")))
    }

    fun isAFunctionalDefinition(command: String):Boolean {
        val tokens = command.split(" ")
        return (tokens.size == 2 && (tokens[1].contains("-->")))
    }

}