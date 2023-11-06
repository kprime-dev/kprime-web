package it.unibz.krdb.kprime.domain.cmd.rdf

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdImportOntouml : TraceCmd {
    override fun getCmdName(): String {
        return "import-ontouml"
    }

    override fun getCmdDescription(): String {
        return "Import as schema from TTL file using ontouml labels."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LABEL,
            TraceCmd.Topic.DATABASE,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val iriContext = context.env.prjContextIRI.value
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val db = context.pool.rdfService.toDatabaseFromOntouml(iriContext,rdfDataDir,addLabels = true)
        context.env.database = db
        return TraceCmdResult() message "Ok. DB imported with ${db.schema.tables().size} tables."
    }

}