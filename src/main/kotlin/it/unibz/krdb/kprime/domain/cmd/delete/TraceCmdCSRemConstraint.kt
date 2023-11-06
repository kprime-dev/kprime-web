package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdCSRemConstraint: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rem-cs-constraint"
    }

    override fun getCmdDescription(): String {
        return "Removes one constraint to a table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <constraint-id>"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        TODO("Not yet implemented")
    }
}