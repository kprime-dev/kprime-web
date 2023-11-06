package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdCSAddTable : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-table"
    }

    override fun getCmdDescription(): String {
        return "Add one table to current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<col-name>,...,<col-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,table,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val tableString = command.split(" ")[1]
        val table = SchemaCmdParser.parseTable(tableString)
        changeSet plus table
        return TraceCmdResult() message "Add table to changeset executed."
    }

}