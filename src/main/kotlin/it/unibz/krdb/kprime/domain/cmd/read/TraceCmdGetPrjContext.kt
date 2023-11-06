package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.term.LabelField

object TraceCmdGetPrjContext: TraceCmd {
    override fun getCmdName(): String {
        return "context"
    }

    override fun getCmdDescription(): String {
        return "Get one context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.CONTEXT,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { CONTEXT_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.CONTEXT_NAME.name, "Context name.") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextName =  args[ArgNames.CONTEXT_NAME.name] as String
        val project = context.pool.prjContextService.projectByName(prjContextName)

        val subject = LabelField(":${prjContextName}")
        val predicate = LabelField("_")
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val labels = context.pool.rdfService.find(iriContext = "",
            subject, predicate, "_",
            rdfDataDir = rdfDataDir).getOrDefault("")
        return if (project!=null) TraceCmdResult()  message successMessage(project,labels) payload project
                    else TraceCmdResult() failure "Context $prjContextName not found."
    }

    private fun successMessage(project: PrjContext, labels:String): String {
        return project.toString() + System.lineSeparator() + labels + System.lineSeparator()
    }

}