
package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.adapter.fact.FactDescriptor
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListFacts : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "facts"
    }

    override fun getCmdDescription(): String {
        return "List all facts from current database, or about a specific table-name."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [<table-name>]"
    }

    override fun getCmdTopics(): String {
        return "read,conceptual,fact"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        var filter = ""
        if (tokens.size==2) filter = tokens[1]
        val database = context.env.database
        var facts = FactDescriptor().describe(database,filter)
        return TraceCmdResult() message facts
    }

}


