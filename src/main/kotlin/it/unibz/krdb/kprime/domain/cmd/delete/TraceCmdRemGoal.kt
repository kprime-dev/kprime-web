package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentLong

object TraceCmdRemGoal : TraceCmd {
    override fun getCmdName(): String {
        return "rem-goal"
    }

    override fun getCmdDescription(): String {
        return "Removes one goal."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.GOAL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { GOAL_ID }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentLong(ArgNames.GOAL_ID.name, "Goal ID") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val goalId =  args[ArgNames.GOAL_ID.name] as Long
        return if (context.pool.todoService.deleteId(context.env.prjContextLocation.value,goalId))
            TraceCmdResult() message "Goal $goalId deleted."
        else
            TraceCmdResult() message "Goal $goalId to remove not found."
    }

}
