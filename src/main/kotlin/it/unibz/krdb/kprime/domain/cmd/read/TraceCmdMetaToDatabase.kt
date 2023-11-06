package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdMetaToDatabase: TraceCmd {
    override fun getCmdName(): String {
        return "meta-to-db"
    }

    override fun getCmdDescription(): String {
        return "Writes meta info from current source to current database."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    private enum class ArgNames { source, table, catalog, schema }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.source.name, "Source to update.") required true,
            TraceCmdArgumentText(ArgNames.table.name, "Name of the table to extract meta info.") required false,
            TraceCmdArgumentText(ArgNames.catalog.name, "Name of the catalog to extract meta info.") required false,
            TraceCmdArgumentText(ArgNames.schema.name, "Name of the schema to extract meta info.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val sourceName =  args[ArgNames.source.name] as String
        val tableName = args[ArgNames.table.name] as String? ?: ""
        val catalogName = args[ArgNames.catalog.name] as String?
        val schemaName = args[ArgNames.schema.name] as String?

//        var workingDataSource = context.pool.sourceService.newWorkingDataSourceOrH2(sourceName)
//        println(workingDataSource)
        val workingDataSource = context.pool.sourceService.getContextDataSourceByName(context.env.prjContextName,sourceName)
            ?: return TraceCmdResult() failure "Source not found $sourceName"
        val metaDatabase = MetaSchemaJdbcAdapter().metaDatabase(workingDataSource,
            Database().withGid(),
            tableName, catalogName, schemaName)
//        println("Trace meta for metadb [${metaDatabase!=null}]")
//        println(metaDatabase)
        metaDatabase.source = sourceName
        metaDatabase.name = "base"
        context.env.database = metaDatabase
        return TraceCmdResult() message context.pool.dataService.prettyDatabase(metaDatabase)
    }
}