package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdSetDatatype: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "set-datatype"
    }

    override fun getCmdTopics(): String {
        return "write,logic,term"
    }

    override fun getCmdDescription(): String {
        return "set datatype of one column"
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table> <column> <datatype>"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        val tableName = tokens[1]
        val columnName = tokens[2]
        val datatype = tokens[3]

        val table = context.env.database.schema.table(tableName) ?: return TraceCmdResult() failure "Table unknonw."
        val colByName = table.colByName(columnName) ?: return TraceCmdResult() failure  "Column unknown."
        colByName.dbtype=datatype
        return TraceCmdResult() message "DataType setted."
    }
}