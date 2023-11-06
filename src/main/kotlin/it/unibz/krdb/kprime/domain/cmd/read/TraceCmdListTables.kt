package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Column
import java.util.*

object TraceCmdListTables : TraceCmd {
    override fun getCmdName(): String {
        return "tables"
    }

    override fun getCmdDescription(): String {
        return "List tables from current database."
    }

    override fun getCmdUsage(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.TABLE,
            TraceCmd.Topic.LOGICAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val database = context.env.database
        val tables = database.schema.tables()
        var result = "Tables:"+System.lineSeparator()
        for (table in tables) {
            result += "${table.name}(${table.primaryKey}/${table.naturalKey})  :" + printCols(table.columns) + System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

    private fun printCols(columns: ArrayList<Column>): String {
        return columns.joinToString(",") { col -> " ${col.name} " }
    }
}
