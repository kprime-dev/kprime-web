package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.term.Term

object TraceCmdGetTerm: TraceCmd {
    override fun getCmdName(): String {
        return "term"
    }

    override fun getCmdDescription(): String {
        return "Get one term details."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.TERM,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { TERM_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TERM_NAME.name, "Term name.") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val termName =  args[ArgNames.TERM_NAME.name] as String

        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val traceName = context.env.currentTrace
        val traceFileName = context.env.currentTraceFileName ?: return TraceCmdResult() failure "No current trace file."

        context.pool.termService.getTermByName(traceName, traceFileName, contextLocation.value, termName).fold(
            onSuccess = { term -> return TraceCmdResult() message successMessage(term) payload term },
            onFailure = { return TraceCmdResult() failure it.message.toString() }
        )

    }

    private fun successMessage(term: Term): String {
            val result = """
            ${term.name}:${term.type} - ${term.gid}
            url:${term.url}
            
            category:[${term.category}]
            ${term.description}
            constraint:[${term.constraint}]
            constraint:[${term.labels}]
            constraint:[${term.properties}]
            -----
        """.trimIndent()
        return (result)
    }

}