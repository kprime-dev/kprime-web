package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.term.Term

object TraceCmdListTerms: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "terms"
    }

    override fun getCmdDescription(): String {
        return "List all terms."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.TERM,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val traceName = context.env.currentTrace ?: return TraceCmdResult() failure "No current trace."
        val traceFileName = context.env.currentTraceFileName ?: return TraceCmdResult() failure "No current trace file."
        val terms = context.pool.termService.getAllTerms(traceName, traceFileName, contextLocation.value)
        return TraceCmdResult()  message successMessage(terms) payload terms
    }

    private fun successMessage(terms:List<Term>):String {
        var result = "Ok. ${terms.size} terms. " + System.lineSeparator()
        result += terms.sortedBy { it.name }.map { term -> "${term.name} ${term.category}" }.joinToString(System.lineSeparator())
        return result
    }

}