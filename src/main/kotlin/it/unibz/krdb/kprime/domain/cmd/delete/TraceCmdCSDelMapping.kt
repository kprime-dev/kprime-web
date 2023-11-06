package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.DropMapping

object TraceCmdCSDelMapping: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "del-cs-mapping"
    }

    override fun getCmdDescription(): String {
        return "Delete one mapping via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <mapping-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val dropMapping = DropMapping() withName command.split(" ")[1]
        context.env.changeSet minus dropMapping
        return TraceCmdResult() message "Delete mapping via changeset executed."
    }
}