package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.*
import java.util.*

object TraceCmdSetGoal : TraceCmd {
    override fun getCmdName(): String {
        return "set-goal"
    }

    override fun getCmdDescription(): String {
        return "Set one goal attributes. \n e.g. >set-goal 1 -priority=3"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.GOAL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { GOAL_ID, title, priority, estimate, assignee, open, completed, duedate }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentLong(ArgNames.GOAL_ID.name, "Goal ID") required true,
            TraceCmdArgumentFreeText(ArgNames.title.name, "Title") required false,
            TraceCmdArgumentInteger(ArgNames.priority.name, "Priority") required false,
            TraceCmdArgumentInteger(ArgNames.estimate.name, "Estimate") required false,
            TraceCmdArgumentText(ArgNames.assignee.name, "Assignee") required false,
            TraceCmdArgumentBoolean(ArgNames.open.name, "Is Open") required false,
            TraceCmdArgumentBoolean(ArgNames.completed.name, "Is Completed") required false,
            TraceCmdArgumentDate(ArgNames.duedate.name, "Due date") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val goalId =  args[ArgNames.GOAL_ID.name] as Long
        val title = args[ArgNames.title.name] as String?
        val priority = args[ArgNames.priority.name] as Int?
        val estimate = args[ArgNames.estimate.name] as Int?
        val assignee = args[ArgNames.assignee.name] as String?
        val open = args[ArgNames.open.name] as Boolean?
        val completed = args[ArgNames.completed.name] as Boolean?
        val duedate = args[ArgNames.duedate.name] as Date?

        val prjContextLocation = context.env.prjContextLocation
        if (prjContextLocation.isEmpty()) TraceCmdResult() failure  "Context is required to remove a goal."

        var changed = ""
        context.pool.todoService.getId(prjContextLocation.value, goalId)
            .onFailure {  return TraceCmdResult() failure  "Goal $goalId to update not found." }
            .onSuccess {    goalById ->
                if (title!=null) { goalById.title = title; changed+="title " }
                if (priority!=null) { goalById.priority = priority; changed+="priority " }
                if (estimate!=null) { goalById.estimate = estimate; changed+="estimate " }
                if (assignee!=null) { goalById.assignee = assignee; changed+="assignee " }
                if (open!=null) { goalById.isOpened = open; goalById.isClosed = !open; changed+="open " }
                if (completed!=null) { goalById.completed = completed; changed+="comleted " }
                if (duedate!=null) { goalById.dateDue = duedate; changed+="duedate " }
                if (changed.isNotEmpty())
                    context.pool.todoService.update(prjContextLocation,goalById)
            }
        return if (changed.isNotEmpty()) TraceCmdResult() message "Goal $goalId $changed setted."
        else TraceCmdResult() warning "Nothing Changed"
    }

}
