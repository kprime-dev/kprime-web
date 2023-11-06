package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import unibz.cs.semint.kprime.domain.datasource.DataSource

object TraceCmdImportSQL : TraceCmd {

    override fun getCmdName(): String {
        return "import-sql"
    }

    override fun getCmdDescription(): String {
        return "Import SQL file from source."
    }

    private enum class ArgNames { SQL_FILE_NAME,SOURCE_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.SOURCE_NAME.name, "Source name to load.") required true,
            TraceCmdArgumentFilePath(ArgNames.SQL_FILE_NAME.name, "SQL file name to import.") required true
        )
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.DATA,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val sqlFileName = args[ArgNames.SQL_FILE_NAME.name] as String
        val sourceName = args[ArgNames.SOURCE_NAME.name] as String
        val prjContextLocation = context.env.prjContextLocation
        val source = context.pool.sourceService.getContextDataSourceByName(context.env.prjContextName,sourceName)
            ?: return TraceCmdResult() failure "Source unknown [$sourceName]."
        context.pool.dataService.createTableFromFileSql(
            prjContextLocation.value,
            sqlFileName,
            source
        )
//            .fold(
//            onFailure = {return TraceCmdResult() failure "Can't open $sqlFileName. (${it.message})" },
//            onSuccess = { successMessage -> return TraceCmdResult() message successMessage }
//        )
        return TraceCmdResult() message "SQL imported in source [$sourceName]."
    }

}