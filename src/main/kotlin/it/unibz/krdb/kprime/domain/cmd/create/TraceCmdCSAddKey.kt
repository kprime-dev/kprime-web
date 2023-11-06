package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdCSAddKey: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-key"
    }

    override fun getCmdDescription(): String {
        return "Adds one key constraint to a table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<table-col-names>.."
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val table = SchemaCmdParser.parseTable(command.split(" ")[1])
        val keyConstraint = context.env.database.schema.buildKey(table.name,table.columns.toSet(),Constraint.TYPE.PRIMARY_KEY.name)
        context.env.changeSet plus keyConstraint
        return TraceCmdResult() message "Add key to changeset executed."
    }
}