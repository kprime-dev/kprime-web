package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddTable
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdSuggest: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "suggest"
    }

    override fun getCmdDescription(): String {
        return "Suggest actions to take."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assist"
    }

    private val BREAK = System.lineSeparator()

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        var suggestions = ""
        val tables = context.env.database.schema.tables
        if ((tables ==null) ||
             (tables.isEmpty())) {
            suggestions += "Start adding one table with command '${TraceCmdAddTable.getCmdName()}'." + BREAK
            suggestions += "or Start adding facts with command 'add-fact'." + BREAK
        } else {
            suggestions += "To validate current database use command '${TraceCmdValidate.getCmdName()}" + BREAK
        }
        suggestions += "To list commands enter '??' or 'help' .$BREAK"
        suggestions += "To list command info enter '<command> ?' or '<command> ??' .$BREAK"
        return TraceCmdResult() message suggestions
    }
}