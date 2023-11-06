package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.DropConstraint
import unibz.cs.semint.kprime.domain.ddl.DropTable

object TraceCmdCSDelTable: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "del-cs-table"
    }

    override fun getCmdDescription(): String {
        return "Delete one table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val dropTable = DropTable()
        val tableNameToDel = command.split(" ")[1]
        dropTable.tableName= tableNameToDel
        context.env.changeSet minus dropTable
        val dropConstraint = DropConstraint() table tableNameToDel
        context.env.changeSet minus dropConstraint
        return TraceCmdResult() message "Delete table via changeset executed."
    }
}