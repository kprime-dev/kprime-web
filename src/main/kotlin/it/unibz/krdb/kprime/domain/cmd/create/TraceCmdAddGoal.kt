package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import java.util.*

object TraceCmdAddGoal: TraceCmd {
    override fun getCmdName(): String {
        return "add-goal"
    }

    override fun getCmdDescription(): String {
        return "Add one goal to current trace."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.GOAL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { GOAL_TITLE }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFreeText(ArgNames.GOAL_TITLE.name, "Goal small description.") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val goalTitle =  args[ArgNames.GOAL_TITLE.name] as String

        val newGoal = Todo(
            0,
            key = context.env.currentTrace.value,
            title = goalTitle,
            completed = false,
            hidden = false,
            dateCreated = Date(),
            dateOpened = Date(),
            dateClosed = Date(),
            dateDue= Date(),
            priority = 0,
            estimate = 0,
            partof = "",
            assignee = "",
            isOpened = false,
            isClosed = false
        )
        newGoal.resetLabels("")

        val prjContextLocation = context.env.prjContextLocation

        return context.pool.todoService.add(prjContextLocation.value, newGoal).fold(
            onSuccess = { TraceCmdResult() message "Added ${it}" payload newGoal },
            onFailure = { TraceCmdResult() failure "Goal not added [${it.message}]." }
        )
    }

}