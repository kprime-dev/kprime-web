package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.trace.TraceName
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdRemTable : TraceCmd {
    override fun getCmdName(): String {
        return "rem-table"
    }

    override fun getCmdDescription(): String {
        return "Removes one table from current model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.TABLE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database: Database = context.env.database
        val tableName = command.split(" ")[1]
        val tableOid = database.tableGid(tableName)
        database.schema.dropTable(tableName)
        return TraceCmdResult() message "Drop table ${command} executed." oid tableOid
    }

}
