package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.trace.TraceFileName

object TraceCmdSqlCreateTableFromCsv : TraceCmd {
    override fun getCmdName(): String {
        return "sql-create-table-from-csv"
    }

    override fun getCmdDescription(): String {
        return "Creates a physical database table starting from data file in CSV format."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.DATA,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { TABLE_NAME, CSV_FILE_PATH, source }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TABLE_NAME.name, "New table name.") required true,
            TraceCmdArgumentFilePath(ArgNames.CSV_FILE_PATH.name, "File path to reach the CSV file to load.") required true,
            TraceCmdArgumentText(ArgNames.source.name, "Source name.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val tableName = args[ArgNames.TABLE_NAME.name] as String
        val csvFilePath = args[ArgNames.CSV_FILE_PATH.name] as String
        val sourceName = args[ArgNames.source.name] as String? ?: ""

        val prjContextLocation = context.env.prjContextLocation

        val workingDataDir = prjContextLocation.value+TraceFileName(csvFilePath).getTraceName().toDirName()
        println("TraceCmdCreateTableFromCsv.workingDataDir: [$workingDataDir]")
        val fileName = TraceFileName(csvFilePath).getFileName()
        println("TraceCmdCreateTableFromCsv.fileName: [$fileName]")
        val datasource = context.pool.sourceService.newWorkingDataSourceOrH2(sourceName)
        runCatching {
        context.pool.dataService.createTableFromFileCsv(
            workingDataDir = workingDataDir,
            filename = fileName,
            datasource = datasource,
            tableName = tableName)
        }.fold(
            onFailure = { return TraceCmdResult() failure "CSV not loaded [${it.message}]." },
            onSuccess = { return TraceCmdResult() message  "CSV loaded on table $sourceName $tableName ."}
        )
    }
}