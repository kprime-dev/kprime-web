package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentURL


object TraceCmdRemContextNamespace: TraceCmd {
    override fun getCmdName(): String {
        return "rem-context-namespace"
    }

    override fun getCmdDescription(): String {
        return "Remove one label namespace to current context repository."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.LABEL
        ).joinToString()
    }

    private enum class ArgNames { NAMESPACE }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentURL(ArgNames.NAMESPACE.name, "namespace to remove") required true,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val namespaceToRemove = args[ArgNames.NAMESPACE.name] as String
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val successMessageInstance =context.pool.rdfService
            .removeNamespace(namespaceToRemove, rdfDataDir)
            .onFailure {
                return TraceCmdResult() failure "Can't remove the namespace [$namespaceToRemove]. ${it.message}"
            }.getOrElse { "Unknown result for remove namespace [$namespaceToRemove]." }
        return TraceCmdResult() message successMessageInstance
    }
}