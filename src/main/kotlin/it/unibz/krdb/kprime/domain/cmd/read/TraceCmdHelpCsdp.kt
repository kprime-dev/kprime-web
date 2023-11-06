package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdHelpCsdp: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "help-csdp"
    }

    override fun getCmdDescription(): String {
        return "List Conceptual Schema Design Procedure steps."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assistance"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        return TraceCmdResult() message """
            The conceptual schema design procedure (CSDP)
            1 Transform familiar information examples into elementary facts, and apply quality checks
            2 Draw the fact types, and apply a population check
            3 Check for entity types that should be combined, and note any arithmetic derivations
            4 Add uniqueness constraints, and check arity of fact types
            5 Add mandatory role constraints, and check for logical derivations
            6 Add value, set comparison and subtyping constraints
            7 Add other constraints and perform final checks
        """.trimIndent()
    }
}