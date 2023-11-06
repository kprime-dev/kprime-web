package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.datasource.DataSource

object TraceCmdTraceSource : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trace-source"
    }

    override fun getCmdDescription(): String {
        return "Create a trace with a database extracted from the source indicated."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <source-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val sourceName = command.split(" ")[1]

        val currentDatabaseSource = context.env.database.source
        val datasource: DataSource = context.env.datasource
                ?: context.pool.sourceService.newWorkingDataSourceOrH2(currentDatabaseSource)
        val tracesDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR

        val dbName = context.pool.dataService.writeDatabaseFileFromDataSource(tracesDir, sourceName, datasource)

        context.env.datasource = datasource
        context.env.currentTrace = TraceName(tracesDir)
        context.env.currentTraceFileName = dbName

        return TraceCmdResult() message "Source traced."
    }

}
