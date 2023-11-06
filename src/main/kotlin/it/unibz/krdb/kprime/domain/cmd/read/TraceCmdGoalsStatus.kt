package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentInteger
import java.util.*

object TraceCmdGoalsStatus: TraceCmd {
    override fun getCmdName(): String {
        return "goals-status"
    }

    override fun getCmdDescription(): String {
        return "List all goal status."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.GOAL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { daysalert }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentInteger(ArgNames.daysalert.name, "Day distance fro alert.") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val daysalert =  args[ArgNames.daysalert.name] as Int? ?: 7

        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."

        val goalsStatus = context.pool.todoService.status(contextLocation, Date(), daysAlert = daysalert)
        return TraceCmdResult()  message goalsStatus
    }

}