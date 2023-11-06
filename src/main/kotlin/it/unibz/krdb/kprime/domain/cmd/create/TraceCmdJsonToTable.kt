package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.trace.TraceFileName

object TraceCmdJsonToTable : TraceCmd {
    override fun getCmdName(): String {
        return "json-create-tables"
    }

    override fun getCmdDescription(): String {
        return "Creates a logical database set of tables starting from data file in JSON format."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.DATA,
            TraceCmd.Topic.LOGICAL).joinToString()
    }

    private enum class ArgNames { JSON_FILE_PATH, source }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.JSON_FILE_PATH.name, "File path to reach the CSV file to load.") required true,
            TraceCmdArgumentText(ArgNames.source.name, "Source name.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val jsonFilePath = args[ArgNames.JSON_FILE_PATH.name] as String
        val source = args[ArgNames.source.name] as String? ?: ""

        val prjContextLocation = context.env.prjContextLocation

        val workingDataDir = prjContextLocation.value+TraceFileName(jsonFilePath).getTraceName().toDirName()
        println("TraceCmdCreateTableFromJson.workingDataDir: [$workingDataDir]")
        val fileName = TraceFileName(jsonFilePath).getFileName()
        println("TraceCmdCreateTableFromJson.fileName: [$fileName]")
        val datasource = context.pool.sourceService.newWorkingDataSourceOrH2(source)
        runCatching {
        context.pool.dataService.createTableFromFileJson(
            workingDataDir = workingDataDir,
            filename = fileName,
            datasource = datasource)
        }.fold(
            onFailure = { return TraceCmdResult() failure "JSON not loaded [${it.message}]." },
            onSuccess = { return TraceCmdResult() message  "JSON loaded on table $source $it."}
        )
    }
}