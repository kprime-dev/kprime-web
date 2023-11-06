package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.nextGid

object TraceCmdToChangeSet: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "to-changeset"
    }

    override fun getCmdDescription(): String {
        return "Generate a changeset starting from current database."
    }

    override fun getCmdUsage(): String {
        return "to-changeset"
    }

    override fun getCmdTopics(): String {
        return "write,logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        val database = context.env.database
        val newChangeSet = ChangeSet()
        newChangeSet.createTable.addAll(database.schema.tables())
        newChangeSet.gid = nextGid()
        context.env.changeSet = newChangeSet
        return TraceCmdResult() message "OK. new changeset created."
    }
}