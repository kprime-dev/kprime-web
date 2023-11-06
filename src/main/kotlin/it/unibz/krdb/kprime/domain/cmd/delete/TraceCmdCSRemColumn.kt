package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdCSRemColumn: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rem-cs-column"
    }

    override fun getCmdDescription(): String {
        return "Removes one column to a table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name> <table-col-name>"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        TODO("Not yet implemented")
    }
}