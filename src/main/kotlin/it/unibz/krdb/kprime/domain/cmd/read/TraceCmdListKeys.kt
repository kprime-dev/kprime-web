package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListKeys : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "keys"
    }

    override fun getCmdDescription(): String {
        return "List keys from current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val constraints = database.schema.keys()
        var result = "Keys:"+System.lineSeparator()
        for (constraint in constraints) {
            result += " ${constraint.name} of type ${constraint.type}"+System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

}
