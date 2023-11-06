package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentPath
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddGoal
import it.unibz.krdb.kprime.domain.trace.TraceName

object TraceCmdListStories: TraceCmd {
    override fun getCmdName(): String {
        return "stories"
    }

    override fun getCmdDescription(): String {
        return "List all stories and directories."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.STORY,
            ).joinToString()
    }

    private enum class ArgNames { path }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentPath(ArgNames.path.name, "Filesystem path.") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val traceArg =  args[ArgNames.path.name] as String? ?: ""
        context.pool.storyService.listTrace(contextLocation, TraceName(traceArg).toDirName(), context).fold(
            onSuccess = { result -> return TraceCmdResult() message result },
            onFailure = { return TraceCmdResult() failure "no project '${contextLocation.value}' found." }
        )
    }

}