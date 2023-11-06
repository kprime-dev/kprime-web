package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdRemMapping : TraceCmd {
    override fun getCmdName(): String {
        return "rem-mapping"
    }

    override fun getCmdDescription(): String {
        return "Removes one mapping from current model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <mapping-name>"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.MAPPING,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database: Database = context.env.database
        val mappingName = command.split(" ")[1]
        return if (database.mappings().removeIf { it.name == mappingName })
            TraceCmdResult() message "Drop mapping ${command} executed."
            else TraceCmdResult() failure  "Drop mapping ${command} not executed."
    }

}
