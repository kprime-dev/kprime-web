package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentInteger
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.trace.TraceFileName
import java.io.File

object TraceCmdSqlCreateTableFromCsvNote : TraceCmd {
    override fun getCmdName(): String {
        return "sql-create-table-from-csv-note"
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

    private enum class ArgNames { TABLE_NAME, NOTE_FILE_PATH, NOTE_INDEX_CSV, source }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TABLE_NAME.name, "New table name.") required true,
            TraceCmdArgumentFilePath(ArgNames.NOTE_FILE_PATH.name, "Note File path to reach the CSV file to load.") required true,
            TraceCmdArgumentInteger(ArgNames.NOTE_INDEX_CSV.name, "Story CSV cell index.") required true,
            TraceCmdArgumentText(ArgNames.source.name, "Source name.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val filepath =  args[ArgNames.NOTE_FILE_PATH.name] as String
        val noteIndex = args[ArgNames.NOTE_INDEX_CSV.name] as Int
        val contextName = context.env.prjContextName
        val traceName = TraceFileName(filepath).getTraceName()
        val traceFileName = TraceFileName(filepath)
        context.pool.storyService.readNotes(contextName, traceName, traceFileName, edit=true,).onFailure {
            return TraceCmdResult() failure "No document '${filepath}' found."
        }.onSuccess { noteBook ->
            val csvText = noteBook[noteIndex-1].title
            val tableName = args[ArgNames.TABLE_NAME.name] as String
            val csvTempFile = File.createTempFile("note", "csv")
            //val csvTempFile = File("prova.csv")
            val csvFilePath = csvTempFile.absolutePath
            csvTempFile.writeText(csvText)
            val source = args[ArgNames.source.name] as String? ?: ""
            //val prjContextLocation = context.env.prjContextLocation
            val workingDataDir = TraceFileName(csvFilePath).getTraceName().toDirName()
            println("TraceCmdCreateTableFromCsv.workingDataDir: [$workingDataDir]")
            val fileName = TraceFileName(csvFilePath).getFileName()
            println("TraceCmdCreateTableFromCsv.fileName: [$fileName]")
            val datasource = context.pool.sourceService.newWorkingDataSourceOrH2(source)
            runCatching {
                context.pool.dataService.createTableFromFileCsv(
                    workingDataDir = workingDataDir,
                    filename = fileName,
                    datasource = datasource,
                    tableName = tableName
                )
            }.onFailure { return TraceCmdResult() failure "CSV not loaded [${it.message}]."  }
            return TraceCmdResult() message "CSV loaded on table $source $tableName ."
        }
        return TraceCmdResult() failure "CSV ${filepath} ${noteIndex} not loaded."
    }
}