package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdCurrentDatabase : TraceCmd {

    override fun getCmdName(): String {
        return "current-database"
    }

    override fun getCmdDescription(): String {
        return "Set current database from current trace."
    }

    override fun getCmdUsage(): String {
        return "usage: current-database <trace> <database-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")

        val (tmpTrace, databaseName) = TraceCmd.traceAndFile(context.env.currentTrace, tokens)

        if (tmpTrace==null) return TraceCmdResult() failure "Trace undefined." //options TraceCmd.listTraces(context)
        val traceDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + tmpTrace
        if (!File(traceDir).exists()) return TraceCmdResult() failure "Trace not found." //options TraceCmd.listTraces(context)
        context.env.currentTrace = tmpTrace

        val sourceTraceFileName = "$traceDir/$databaseName"
        if (!File(sourceTraceFileName).isFile) return TraceCmdResult() failure "Database not found." //options TraceCmd.listDatabases(traceDir, tmpTrace)
        val xmlDatabase = File(sourceTraceFileName).readText(Charsets.UTF_8)
        context.env.database = context.pool.dataService.databaseFromXml(xmlDatabase)
        if (context.env.database.source.isNotEmpty()) {
            context.env.datasource = context.pool.sourceService.newWorkingDataSourceOrH2(context.env.database.source)
        }
        return TraceCmdResult() message "Setted ${databaseName} ." trace tmpTrace file databaseName
    }


}

