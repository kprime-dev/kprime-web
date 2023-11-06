package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdRemPrjContext: TraceCmd {
    override fun getCmdName(): String {
        return "rem-context"
    }

    override fun getCmdDescription(): String {
        return "Remove one context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
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
        return context.pool.prjContextService.remProject(prjContextName).fold(
            onSuccess = { TraceCmdResult() message "Removed $it context." },
            onFailure = { TraceCmdResult() failure "Context not removed [${it.message}]." }
        )
    }

}