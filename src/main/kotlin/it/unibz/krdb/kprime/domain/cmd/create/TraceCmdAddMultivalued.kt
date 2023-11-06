package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddMultivalued : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-multivalued"
    }

    override fun getCmdDescription(): String {
        return "Add one multi-value dependency to current relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <tab-name>:<source-attr-names>--><target-attr-names>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        database.schema.addMultivalued(commandWithoutPrefix)
        return TraceCmdResult() message "MVDependency added."
    }

}