package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddInclusion : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-inclusion"
    }

    override fun getCmdDescription(): String {
        return "Adds inclusion constraint to relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <source-tab-name>:<attr-names>--><target-tab-name>:<attr-names>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        database.schema.addInclusion(commandWithoutPrefix)
        return TraceCmdResult() message "Add inclusion executed."
    }

}
