package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdHelpAggregate: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "help-aggregate"
    }

    override fun getCmdDescription(): String {
        return "List DDD Aggregates Rules of Thumb."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assistance"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        return TraceCmdResult() message """
            The Aggregates Rules of Thumb :
            1. Design small Aggregates.
            2. Protect business invriants  inside Aggregates boundaries.
            3. Update other Aggregates using eventual consistency.
            4. Reference other Aggregates by identity only.
        """.trimIndent()
    }
}