package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdSetAsActive: TraceCmd {
    override fun getCmdName(): String {
        return "set-as-active"
    }

    override fun getCmdDescription(): String {
        return "Set current trace database as active into current project"
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.PROJECT).joinToString()
    }

    private enum class argNames { BOOLEAN }
    private val argConstr = mapOf<argNames, TraceCmdArgumentI>(
        argNames.BOOLEAN to (TraceCmdArgumentText(argNames.BOOLEAN.name,"true to activate or false otherwise",4,5) required true)
    )

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return argConstr.values.toList()
    }


    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
//        println("TraceCmdMeta executeMap:")
//        println(args)
        val isToActivate =  args[argNames.BOOLEAN.name] ?: "false"
        val prjContextName = context.env.prjContextName.value
        println(prjContextName)
        val prjContext = context.pool.prjContextService.projectByName(prjContextName)
                ?: return TraceCmdResult() failure "No project $prjContextName found."
        val currentTrace = context.env.currentTrace
        val currentTermBase = context.env.currentTraceFileName ?: ""
        if (currentTrace.isEmpty() || currentTermBase.isEmpty())
            return TraceCmdResult() failure "No project term Trace or term Base found."
        val newPrjContext =
            if (isToActivate=="true") prjContext.copy(activeTrace = currentTrace.value,activeTermBase = currentTermBase)
            else  prjContext.copy(activeTrace = TraceName.NO_TRACE_NAME.value,activeTermBase = "")
        context.pool.prjContextService.update(newPrjContext)
        return TraceCmdResult() message "Ok. Set $currentTrace, $currentTermBase as active [$isToActivate] for current project $prjContextName. "
    }
}