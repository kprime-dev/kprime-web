package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.todo.Todo

object TraceCmdListGoals: TraceCmd {
    override fun getCmdName(): String {
        return "goals"
    }

    override fun getCmdDescription(): String {
        return "List all goals."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.GOAL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val goals = context.pool.todoService.all(contextLocation.value)
        return TraceCmdResult()  message successMessage(goals) payload goals
    }

    private fun successMessage(goals: List<Todo>): String {
        var result = "Ok. ${goals.size} goals. " + System.lineSeparator()
        result += goals.map { goal -> "(${goal.id}) ${goal.title}" }
            .joinToString(System.lineSeparator())
        return result
    }

}