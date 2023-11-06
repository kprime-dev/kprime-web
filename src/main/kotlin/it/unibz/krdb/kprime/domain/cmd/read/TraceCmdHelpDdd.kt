package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdHelpDdd: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "help-ddd"
    }

    override fun getCmdDescription(): String {
        return "List Domain Driven Design steps."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assistance"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        return TraceCmdResult() message """
            The Domain Driven Design (DDD) steps :
            1. Focus on business events and workflows rather than data structures.
            2. Partition the problem domain into smaller subdomains.
            3. Create a model of each subdomain in the solution.
            4. Develop a common language (known as the “Ubiquitous Language”) used everywhere.
        """.trimIndent()
    }
}