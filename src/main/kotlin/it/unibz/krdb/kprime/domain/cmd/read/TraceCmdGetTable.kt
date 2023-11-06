package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.adapter.fact.FactDescriptor
import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Table

object TraceCmdGetTable: TraceCmd {
    override fun getCmdName(): String {
        return "table"
    }

    override fun getCmdDescription(): String {
        return "Get one table."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.TABLE,
            TraceCmd.Topic.LOGICAL).joinToString()
    }

    private enum class ArgNames { TABLE_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TABLE_NAME.name, "Table name") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val tableName =  args[ArgNames.TABLE_NAME.name] as String
        val database = context.env.database
        val table = database.schema.table(tableName)
        val constraints = database.schema.constraintsByTable(tableName)
        val facts = FactDescriptor().describe(database,tableName)
        return if (table==null) {
            TraceCmdResult() failure "Table $tableName not found."
        } else {
            TraceCmdResult()  message successMessage(context.pool.jsonService,table,constraints,facts) payload table
        }
    }

    private fun successMessage(jsonService: JsonService,
                               table: Table,
                               constraints: List<Constraint>,
                               facts: String): String {
        return jsonService.toJson(facts)+jsonService.toJson(constraints)
    }

}