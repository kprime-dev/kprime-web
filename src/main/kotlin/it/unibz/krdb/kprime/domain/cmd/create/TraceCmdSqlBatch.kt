package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.trace.TraceFileName

object TraceCmdSqlBatch : TraceCmd {
    override fun getCmdName(): String {
        return "sql-batch"
    }

    override fun getCmdDescription(): String {
        return "Executes a SQL batch."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.DATA,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { SQL_FILE_PATH, source }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.SQL_FILE_PATH.name, "File path to reach the CSV file to load.") required true,
            TraceCmdArgumentText(ArgNames.source.name, "Source name.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val csvFilePath = args[ArgNames.SQL_FILE_PATH.name] as String
        val source = args[ArgNames.source.name] as String? ?: ""

        val prjContextLocation = context.env.prjContextLocation

        val workingDataDir = prjContextLocation.value+TraceFileName(csvFilePath).getTraceName().toDirName()
        println("TraceCmdSqlBatch.workingDataDir: [$workingDataDir]")
        val fileName = TraceFileName(csvFilePath).getFileName()
        println("TraceCmdSqlBatch.fileName: [$fileName]")
        val datasource = context.pool.sourceService.newWorkingDataSourceOrH2(source)
        runCatching {
        context.pool.dataService.createTableFromFileSql(
            workingDataDir = workingDataDir,
            filename = fileName,
            datasource = datasource)
        }.fold(
            onFailure = { return TraceCmdResult() failure "SQL file not loaded [${it.message}]." },
            onSuccess = { return TraceCmdResult() message  "SQL file loaded on $source."}
        )
    }
}