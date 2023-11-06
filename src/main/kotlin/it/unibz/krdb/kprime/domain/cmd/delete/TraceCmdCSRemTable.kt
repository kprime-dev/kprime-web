package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.DropTable

object TraceCmdCSRemTable : it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    override fun getCmdName(): String {
        return "rem-cs-table"
    }

    override fun getCmdDescription(): String {
        return "Remove one table to current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,table,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val tableName = command.split(" ")[1]
        val dropTable = DropTable() name tableName
        changeSet minus dropTable
        return TraceCmdResult() message "Remove table to changeset executed."
    }
}