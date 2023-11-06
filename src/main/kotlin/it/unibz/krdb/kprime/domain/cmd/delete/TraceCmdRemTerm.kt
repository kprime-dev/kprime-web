package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdRemTerm: TraceCmd {
    override fun getCmdName(): String {
        return "rem-term"
    }

    override fun getCmdDescription(): String {
        return "Removes one term from current dictionary."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.TERM,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { TERM_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TERM_NAME.name, "Term name") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val termName =  args[ArgNames.TERM_NAME.name] as String
        return context.pool.termService.remTerm(context.env.prjContextLocation.value,termName).fold(
            onSuccess = { TraceCmdResult() message it },
            onFailure = { TraceCmdResult() failure ( it.message ?:" Term $termName Not found.") }
        )
    }

}