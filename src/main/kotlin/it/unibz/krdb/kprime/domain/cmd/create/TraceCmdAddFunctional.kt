package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddFunctional : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-functional"
    }

    override fun getCmdDescription(): String {
        return "Add one functional dependency to current relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <tab-name>:<source-attr-names>--><target-attr-names>"
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
        database.schema.addFunctional(commandWithoutPrefix)
        return TraceCmdResult() message "Functional added."
    }

}