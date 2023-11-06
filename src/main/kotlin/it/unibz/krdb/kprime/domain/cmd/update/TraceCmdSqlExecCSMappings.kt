package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase

object TraceCmdSqlExecCSMappings: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "sql-exec-changeset-mappings"
    }

    override fun getCmdDescription(): String {
        return "Execute current changeset as SQL commands to source."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,physical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        val sqlCommands = SQLizeCreateUseCase().createTableMappings(context.env.changeSet)
        val workingDataSource = context.env.datasource
                ?: context.pool.sourceService.newWorkingDataSourceOrH2(context.env.database.source)
                //?: return RestResult() failure "Datasource null."
        val jdbcAdapter = JdbcAdapter()
        context.env.datasource?.connection?.autocommit = false
        for (sqlCommand in sqlCommands) {
            jdbcAdapter.create(workingDataSource, sqlCommand)
            // TODO add drop, update, insert
        }
        jdbcAdapter.commit(workingDataSource)
        return TraceCmdResult() message "OK. Changeset mappings executed."
    }

}