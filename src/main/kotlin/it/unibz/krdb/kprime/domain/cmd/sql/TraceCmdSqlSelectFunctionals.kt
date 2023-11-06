package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.*
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdSqlSelectFunctionals : TraceCmd {
    override fun getCmdName(): String {
        return "select-functionals"
    }

    override fun getCmdDescription(): String {
        return "Select SQL command to find functional dependencies in a table."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,sql,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database

        val sqlCommandWithOptions = sqlCommandViaEnvelope(context.envelope,command)
        val (args,optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        val tableName = if (args.size<=1) "" else args[1]
        val datasourceNameFromCmd = optionals["source"]?:""
        if (tableName.isEmpty()) return TraceCmdResult() failure "Table name is required."
        println("....................")
        println("tableName [$tableName]")
        println("datasourceNameFromCmd [$datasourceNameFromCmd]")
        println("^^^^^^^^^^^^^^^^^^^^")
        val result = context.pool.dataService
            .queryFunctionals(computeDataSource(context, database, datasourceNameFromCmd),database,tableName)
        return TraceCmdResult() message result
    }

    private fun computeDataSource(
        context: CmdContext,
        database: Database,
        datasourceFromCmd: String
    ): DataSource {
        return context.pool.sourceService.newWorkingDataSource(datasourceFromCmd)
            ?: context.env.datasource
            ?: context.pool.sourceService.newWorkingDataSourceOrH2(database.source)
    }

    private fun sqlCommandViaEnvelope(evelope: CmdEnvelope, sqlCommandWithOptions: String): String {
        return if (evelope.cmdPayload.isNotEmpty()) {
            "SELECT "+evelope.cmdPayload.joinToString(" ")
        } else sqlCommandWithOptions
    }

}
