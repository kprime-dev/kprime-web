package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdReplaceTable : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "replace-table"
    }

    override fun getCmdDescription(): String {
        return "Drops and replace one table in current maodel."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<col-names>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,table"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        val tableName:String = commandWithoutPrefix.split(":")[0]
        database.schema.dropTable(tableName)
        database.schema.addTable(commandWithoutPrefix)
        return TraceCmdResult() message "Alter table ${commandWithoutPrefix} executed."    }

}
