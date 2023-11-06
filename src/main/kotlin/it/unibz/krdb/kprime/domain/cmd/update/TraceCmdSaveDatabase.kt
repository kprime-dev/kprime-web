package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database
import java.io.File

object TraceCmdSaveDatabase: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "save-database"
    }

    override fun getCmdDescription(): String {
        return "Save current database on file."
    }

    override fun getCmdUsage(): String {
        return "save-database"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val database: Database = context.env.database
        context.pool.dataService.writeDatabase(database,context.env.prjContextLocation,context.env.currentTrace)
        return TraceCmdResult() message "Db ${database.name} saved."
    }
}