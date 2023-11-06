package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdHelpEventstorm: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "help-eventstorm"
    }

    override fun getCmdDescription(): String {
        return "List Event Storm terminology."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assistance"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        return TraceCmdResult() message """
            The Event Storm terminiology:
            + event EventName
            + actor ActorName
            + readmodel ModelName
            + command CommandName
            + aggregate AggregateName
            + external ExternalName
            + policy PolicyName
            + goal GoalName
        """.trimIndent()
    }
}