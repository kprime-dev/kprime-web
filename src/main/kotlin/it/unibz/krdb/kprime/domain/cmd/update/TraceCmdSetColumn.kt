package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdSetColumn : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "set-column"
    }

    override fun getCmdDescription(): String {
        return "Set attributes of columns of a table."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + "<table-name>:<col-names> nullable | not-nullable | hidden | not-hidden | name:<name> | unique | not-unique"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database: Database = context.env.database
        val args = command.split(" ")
        val tableTokens = args[1]
        var mandatory = false
        if (args.size==3) {
            mandatory = (args[2].trim().equals("false"))
        }
        val parseTable = SchemaCmdParser.parseTable(tableTokens)

        val tableName = tableTokens.substringBefore(":")
        val colNames = tableTokens.substringAfter(":").split(",")

        val table = database.schema.table(parseTable.name)
                ?: return TraceCmdResult() failure "Table ${parseTable.name} not found."

        for (colName in colNames) {
            val colByName = table.colByName(colName)?:break
            if (args[2]=="nullable") colByName.nullable = true
            if (args[2]=="not-nullable") colByName.nullable = false
            if (args[2].startsWith("name:")) colByName.name = args[2].substringAfterLast(":")
            if (args[2]=="hidden") colByName.addLabels("hidden")
            if (args[2]=="not-hidden") colByName.remLabels(listOf("hidden"))
            if (args[2]=="unique") colByName.addLabels("unique")
            if (args[2]=="not-unique") colByName.remLabels(listOf("not-unique"))
        }

        return TraceCmdResult() message "Set column executed."
    }

}
