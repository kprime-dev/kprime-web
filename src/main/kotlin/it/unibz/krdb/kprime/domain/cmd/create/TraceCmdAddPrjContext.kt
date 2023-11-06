package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.project.PrjContext

object TraceCmdAddPrjContext: TraceCmd {
    override fun getCmdName(): String {
        return "add-context"
    }

    override fun getCmdDescription(): String {
        return "Add one context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONTEXT,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { CONTEXT_NAME, location }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.CONTEXT_NAME.name, "Context name.") required true,
            TraceCmdArgumentText(ArgNames.location.name, "Context location.") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextName =  args[ArgNames.CONTEXT_NAME.name] as String
        val prjContextLocation =  args[ArgNames.CONTEXT_NAME.name] as String? ?: ""

        val newPrjContext = PrjContext(
            name = prjContextName,
            location = prjContextLocation,
        )
        newPrjContext.resetLabels("")

        return context.pool.prjContextService.addProject(newPrjContext).fold(
            onSuccess = { TraceCmdResult() message "Added ${it.name} context." payload newPrjContext },
            onFailure = { TraceCmdResult() failure "Context not added [${it.message}]." }
        )
    }

}