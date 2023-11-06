package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentLong
import it.unibz.krdb.kprime.domain.todo.Todo

object TraceCmdGetGoal: TraceCmd {
    override fun getCmdName(): String {
        return "goal"
    }

    override fun getCmdDescription(): String {
        return "Get one goal."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
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
        val contextLocation = context.env.prjContextLocation.value
        val goalId =  args[ArgNames.GOAL_ID.name] as Long
        return context.pool.todoService.getId(contextLocation,goalId).fold(
            onSuccess = { goal -> TraceCmdResult()  message successMessage(context.pool.jsonService,goal) payload goal },
            onFailure = { error -> TraceCmdResult() failure errorMessage(error) }
        )
    }

    private fun successMessage(jsonService: JsonService, goal: Todo): String {
        return jsonService.toJson(goal)
    }

    private fun errorMessage(error: Throwable): String {
        return error.message?: "Error on finding goal [${error.message}]."
    }
}