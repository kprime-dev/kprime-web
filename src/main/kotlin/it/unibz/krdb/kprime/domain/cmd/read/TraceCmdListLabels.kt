package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListLabels : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "labels"
    }

    override fun getCmdDescription(): String {
        return "List all labels in current context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.LABEL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = context.env.prjContextLocation
        val prjContextName = context.env.prjContextName
        val instanceMessage = context.pool.rdfService.list(
            iriContext = prjContextName.value,
            rdfDataDir = RdfService.getInstanceRdfDataDir(context.pool.settingService.getInstanceDir())
        )
            .onFailure {
                return TraceCmdResult() failure "Can't list Labels. ${it.message}"
            }.getOrElse { "No labels in instance." }
        val contextMessage = context.pool.rdfService.list(
            iriContext = prjContextName.value,
            rdfDataDir = RdfService.getPrjContextRdfDataDir(prjContextLocation)
        ).getOrDefault("No labels in context ${prjContextName.value}.")
        return TraceCmdResult() message computeSuccessMessage(instanceMessage,contextMessage)
    }

    private fun computeSuccessMessage(instanceMessage: String, contextMessage: String): String {
        return """
Instance:
$instanceMessage
Context:
$contextMessage
        """.trimIndent()
    }

}