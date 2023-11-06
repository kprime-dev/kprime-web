package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TraceCmdAddDatabase : TraceCmd {

    override fun getCmdName(): String {
        return "add-database"
    }

    override fun getCmdDescription(): String {
        return "Adds one database new-database-name file into current trace folder or the specified trace-name."
    }

    override fun getCmdUsage(): String {
        return "usage: add-database [<trace-name>] <new-database-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")

        val (tmpTrace, newDb) = TraceCmd.traceAndFile(context.env.currentTrace, tokens)

        if (tmpTrace==null) return TraceCmdResult() failure "Trace undefined." //options TraceCmd.listTraces(context)
        val traceDir = context.pool.settingService.getTracesDir() + "/" + tmpTrace
        if (!File(traceDir).exists()) return TraceCmdResult() failure "Trace not found." //options TraceCmd.listTraces(context)
        context.env.currentTrace = tmpTrace

        val tmpDatabase = Database().withGid()
        tmpDatabase.id = tmpDatabase.gid?:""
        tmpDatabase.author = context.env.author
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        tmpDatabase.time = current.format(formatter)
        val revision = LocalDateTime.now().nano
        val dbName = newDb + "_${revision}_db.xml"
        tmpDatabase.name = dbName

        val fileContent = context.pool.dataService.prettyDatabase(tmpDatabase)
        val fileTraceFileName = "$traceDir/$dbName"
        if (File(fileTraceFileName).isFile) return TraceCmdResult() failure "Database already exists." //options TraceCmd.listDatabases(traceDir, tmpTrace)
        File(fileTraceFileName).writeText(fileContent)

        context.env.currentTrace = tmpTrace
        context.env.database = tmpDatabase

        return TraceCmdResult() message "Db $dbName created."
    }
}