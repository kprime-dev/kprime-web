package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TraceCmdTraceDb : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trace-database"
    }

    override fun getCmdDescription(): String {
        return "Traces current database into a file of current trace."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [tracedbname]"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val sourceContent: String
        val tokens = command.split(" ")
        val database = context.env.database
        if (database.isEmpty()) return TraceCmdResult() failure "no DB"
        if (context.env.currentTrace==null || context.env.currentTrace!!.isEmpty()) return TraceCmdResult() failure "no trace"
        val revision = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))
        //val dbName = database.name.substring(0, 10) + "_${revision}_tracedb.xml"
        var dbName = "revision_${revision}_tracedb.xml"
        if (tokens.size>1) {
            dbName = "${tokens[1]}_tracedb.xml"
        }
        database.name = dbName
        sourceContent = context.pool.dataService.prettyDatabase(database)
        val sourceTraceFileName = context.getCurrentTraceDir() + dbName
        File(sourceTraceFileName).writeText(sourceContent)
        return TraceCmdResult() message "DB traced."
    }

}
