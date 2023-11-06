package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdCSAddInclusion : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-inclusion"
    }

    override fun getCmdDescription(): String {
        return "Add one inclusion constraint to current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<col-name>,...,<col-name>--><table-name>:<col-name>,...,<col-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val constraintString = command.split(" ")[1]
        val index = (context.env.database.schema.constraints?.size?: 0) + context.env.changeSet.size()
        changeSet plus SchemaCmdParser.parseInclusion(index, constraintString)
        return TraceCmdResult() message "Add inclusion constraint to changeset executed."
    }

}