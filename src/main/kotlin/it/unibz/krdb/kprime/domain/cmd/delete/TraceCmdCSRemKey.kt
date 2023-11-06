package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdCSRemKey: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rem-cs-key"
    }

    override fun getCmdDescription(): String {
        return "Removes one key constraint to a table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<column-name>,.."
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        TODO("Not yet implemented")
    }
}