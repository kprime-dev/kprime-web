package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.AlterTable

object TraceCmdCSAddSelf : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-self"
    }

    override fun getCmdDescription(): String {
        return "Adds auto-increment column to specified table in current changeset."
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

        val sqlCommand = "ALTER TABLE $tableName ADD COLUMN self INT NOT NULL AUTO_INCREMENT UNIQUE"
        val alterTable = AlterTable() onTable tableName withStatement sqlCommand
        context.env.changeSet.plus(alterTable)
        return TraceCmdResult() message "Self column added to changeset."
    }

}
