package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.dql.Query

object TraceCmdSetMapping : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "set-mapping"
    }

    override fun getCmdDescription(): String {
        return "Redefine one mapping in current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <mapping-name> <new-definition>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val tokens = command.split(" ")

        val mappingName = tokens[1]
        val mapping = database.mapping(mappingName) ?: return (TraceCmdResult() message "no mapping $mappingName found")

        when(val setCommand = tokens[2]) {
            "select" -> setSelect(mapping,tokens[3])
            "where" -> setWhere(mapping,command)
            else ->  return (TraceCmdResult() message "command $setCommand not active")
        }
        return (TraceCmdResult() message "command ok")
    }


    private fun setWhere(mapping: Query, toWhere: String) {
        val from = toWhere.indexOf(" where ")+7
        mapping.select.where.condition = toWhere.substring(from)
    }

    private fun setSelect(mapping: Query, toSelect: String) {
        mapping.select.attributes.clear()
        mapping.select.addAttributes(toSelect.split(","))
    }
}