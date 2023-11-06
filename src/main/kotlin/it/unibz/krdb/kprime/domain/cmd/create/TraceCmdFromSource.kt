package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdFromSource: TraceCmd {
    override fun getCmdName(): String {
        return "from-source"
    }

    override fun getCmdDescription(): String {
        return "Reverse a database from current source."
    }

    override fun getCmdUsage(): String {
        return "from-source"
    }

    override fun getCmdTopics(): String {
        return "write,logical,source"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val workingSource = context.env.datasource
                ?: return TraceCmdResult() failure "No current source."
        val metaDatabase = MetaSchemaJdbcAdapter().metaDatabase(workingSource, Database().withGid(),"",null,null)
        context.env.database = metaDatabase
        return TraceCmdResult() message context.pool.dataService.prettyDatabase(metaDatabase)
    }
}