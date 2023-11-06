package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.service.LiquibaseSQLizeAdapter
import unibz.cs.semint.kprime.domain.db.DataType
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

object TraceCmdSqlExecCS: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "sql-exec-changeset"
    }

    override fun getCmdDescription(): String {
        return "Execute current changeset SQL commands to source."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,physical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (!context.env.changeSet.isEmpty()) {
            if (context.env.changeSet.sqlCommands==null) context.env.changeSet.sqlCommands = mutableListOf()
            val sqlCommands = context.env.changeSet.sqlCommands
                    ?: return TraceCmdResult() failure "Changeset has no sql commands (1)."
            sqlLegalize(context.env.changeSet)
            sqlCommands.addAll(
                    LiquibaseSQLizeAdapter().sqlize(DatabaseTrademark.H2,context.env.changeSet))
        }
        val sqlCommands = context.env.changeSet.sqlCommands
                ?: return TraceCmdResult() failure "Changeset has no sql commands."
        val workingDataSource = context.env.datasource
                ?: context.pool.sourceService.newWorkingDataSourceOrH2(context.env.database.source)
        val jdbcAdapter = JdbcAdapter()
        context.env.datasource?.connection?.autocommit = false
        for (sqlCommand in sqlCommands) {
            jdbcAdapter.create(workingDataSource, sqlCommand)
        }
        jdbcAdapter.commit(workingDataSource)
        return TraceCmdResult() message "OK. Changeset sql commands executed."
    }

    private fun sqlLegalize(changeSet: ChangeSet) {
        for (table in changeSet.createColumn) {
            if (table.name.isEmpty()) throw Exception("Table without a name.")
            for (col in table.columns) {
                if (col.name.isEmpty()) throw Exception("Table column without a name.")
                if (col.dbtable?.isEmpty() != false) col.dbtable = table.name
                if (col.dbname?.isEmpty() != false) col.dbname = col.name
                if (col.dbtype?.isEmpty() != false) col.dbtype = DataType.varchar.name
            }
        }
        for (table in changeSet.createTable) {
            if (table.name.isEmpty()) throw Exception("Table without a name.")
            for (col in table.columns) {
                if (col.name.isEmpty()) throw Exception("Table column without a name.")
                if (col.dbtable?.isEmpty() != false) col.dbtable = table.name
                if (col.dbname?.isEmpty() != false) col.dbname = col.name
                if (col.dbtype?.isEmpty() != false) col.dbtype = DataType.varchar.name
            }
        }
    }

}