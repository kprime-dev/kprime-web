package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdRenameMapping : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rename-mapping"
    }

    override fun getCmdDescription(): String {
        return "Rename one mapping from current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <old-name-mapping> <new-name-mapping>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,mapping"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val tokens = command.split(" ")
        if (tokens.size!=3) return TraceCmdResult() failure "Mapping rename requires 2 args."
        val mappingOldName = tokens[1]
        val mapping = database.mapping(mappingOldName)
        if (mapping==null) return TraceCmdResult() failure "Mapping $mappingOldName not found."
        val mappingNewName = tokens[2]
        mapping.name = mappingNewName
        return TraceCmdResult() message "mapping $mappingOldName renamed in $mappingNewName."
    }

}
