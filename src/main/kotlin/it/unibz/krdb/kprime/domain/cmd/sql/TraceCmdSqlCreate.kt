package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource

open class TraceCmdSqlCreate : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "create"
    }

    override fun getCmdDescription(): String {
        return "Create SQL command."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <sql-args>"
    }

    override fun getCmdTopics(): String {
        return "write,sql,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val (sqlCommandLines,optionals) = TraceCmd.separateArgsOptionals(command)
        val datasourceNameFromCmd = optionals["source"]?:""

        val datasourceName = if (datasourceNameFromCmd.isNotEmpty()) datasourceNameFromCmd
            else database.source
        val projectName = context.env.prjContextName
        val prjContext =
            context.pool.prjContextService.projectByName(projectName.value)
                ?: return TraceCmdResult() failure "Context ${projectName.value} not found."

        val workingDataSource = context.pool.dataService.extractDataSource(prjContext, datasourceName)
            ?: context.env.datasource
            ?: context.pool.sourceService.newWorkingDataSourceOrH2(database.source)

        JdbcAdapter().create(workingDataSource,sqlCommandLines.joinToString(" "))
        return TraceCmdResult() message " ${command} OK "
    }

}
