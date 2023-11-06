package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdRemConstraint : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rem-constraint"
    }

    override fun getCmdDescription(): String {
        return "Removes one constraint from current relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <constraint-name>"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.CONSTRAINT,
            TraceCmd.Topic.TABLE,
            TraceCmd.Topic.DATABASE).joinToString()

    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        database.schema.dropConstraint(commandWithoutPrefix)
        return TraceCmdResult() message "Remove constraint ${command} executed."
    }

}
