package it.unibz.krdb.kprime.domain.cmd.rdf

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI

object TraceCmdImportTTL : TraceCmd {

    override fun getCmdName(): String {
        return "import-ttl"
    }

    override fun getCmdDescription(): String {
        return "Import as schema from TTL file using ontouml labels."
    }

    private enum class ArgNames { TTL_FILE_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.TTL_FILE_NAME.name, "TTL file name to import.") required true
        )
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LABEL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val ttlFileName = args[ArgNames.TTL_FILE_NAME.name] as String
        val iriContext = "${context.env.prjContextIRI.value}${context.env.prjContextName.value}"
        val prjContextLocation = context.env.prjContextLocation
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val source = context.pool.sourceService.getContextSourceByName(context.env.prjContextName,ttlFileName)
        val fileNameToImport = source?.location ?: ttlFileName
        context.pool.rdfService.import(
            iriContext,
            prjContextLocation,
            fileNameToImport,
            rdfDataDir
        ).fold(
            onFailure = {return TraceCmdResult() failure "Can't open $ttlFileName. (${it.message})" },
            onSuccess = { successMessage -> return TraceCmdResult() message successMessage }
        )
    }

}