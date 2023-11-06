package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddParam : it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    override fun getCmdName(): String {
        return "add-param"
    }

    override fun getCmdDescription(): String {
        return "Adds one parameter for transformations."
    }

    override fun getCmdUsage(): String {
        return "add-param <param-name> <param-value>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,transformer"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens: List<String> = command.split(" ")
        val value = command.substring(tokens[0].length+tokens[1].length+2)
        addParam(context,tokens[1], value)
        return TraceCmdResult() message "Parameter ${tokens[1]} = ${value} added."
    }

    private fun addParam(context: CmdContext, key:String, value:String) {
        context.env.currentParams.put(key,listOf(value))
    }

}

