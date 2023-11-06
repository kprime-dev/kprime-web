package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdCSOids : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-oids"
    }

    override fun getCmdDescription(): String {
        return "Adds object ids column to specified table and linked tables adding sql statements into current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>"
    }

    override fun getCmdTopics(): String {
        return "write,sql,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        if (tokens.size < 2) return TraceCmdResult() failure getCmdUsage()
        val tableName = tokens[1].trim()
        if (tableName.isEmpty()) return TraceCmdResult() failure getCmdUsage()

        val oidsChangeset = context.env.database.schema.oidForTable(tableName)
        context.env.changeSet.add(oidsChangeset)

        return TraceCmdResult() message "OIDs columns added to changeset."
    }

}
