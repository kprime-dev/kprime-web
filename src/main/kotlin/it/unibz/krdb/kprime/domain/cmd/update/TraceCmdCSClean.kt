package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.nextGid

object TraceCmdCSClean: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "changeset-clean"
    }

    override fun getCmdDescription(): String {
        return "Clean all content of current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        val changeSet = ChangeSet()
        changeSet.gid = nextGid()
        context.env.changeSet = changeSet
        return TraceCmdResult() message "OK. Changeset cleaned."
    }

}