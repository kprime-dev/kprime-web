package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdNewDb : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "new-db"
    }

    override fun getCmdDescription(): String {
        return "creates a new database, eventually with trace and source names."
    }

    override fun getCmdUsage(): String {
        return "new-db <database-name> [<trace-name> [<source-name>]]"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")

        val dbName = tokens[1]

        val database = unibz.cs.semint.kprime.domain.db.Database().withGid()
        database.name = dbName
        database.schema.name = dbName+"_schema"
        context.env.database = database

        if (context.env.currentTrace==null || context.env.currentTrace!!.isEmpty()) return TraceCmdResult() warning "no trace"

        val dbFileName = "${dbName}_db.xml"
        val fileContent= context.pool.dataService.prettyDatabase(database)
        val sourceTraceFileName = context.getCurrentTraceDir() + dbFileName
        File(sourceTraceFileName).writeText(fileContent)

        return TraceCmdResult() message "DB traced."
    }

}
