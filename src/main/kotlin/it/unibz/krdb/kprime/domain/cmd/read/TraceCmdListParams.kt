package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListParams : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "params"
    }

    override fun getCmdDescription(): String {
        return "List current transform params."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,transform"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        var result = ""
        for (par in context.env.currentParams)
            result += "Parameter ${par.key} = ${par.value} ."+System.lineSeparator()
        return TraceCmdResult() message result
    }

    private fun addParam(context: CmdContext, key:String, value:String) {
        context.env.currentParams.put(key,listOf(value))
    }
}

