package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Column
import java.util.*

object TraceCmdListConstraints : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "constraints"
    }

    override fun getCmdDescription(): String {
        return "List all constraints of current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val constraints = database.schema.constraints()
        var result = "Constraints:"+System.lineSeparator()
        for (constraint in constraints) {
            result += " ${constraint.name}  ${constraint.type} ${printCols(constraint.source.columns)} -> ${printCols(constraint.target.columns)}" + System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

    private fun printCols(columns: ArrayList<Column>): String {
        return columns.map { col -> " ${col.name} " }.joinToString(",")
    }

}
