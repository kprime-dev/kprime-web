package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmd3nf : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "3nf"
    }

    override fun getCmdDescription(): String {
        return "Verify third normal form over functional dependencies."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,normalize"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val relations = database.schema.decompose3NF()
        val violations = database.schema.violations3NF()
        var result = ""
        result += System.lineSeparator()+ "---------violations:"+ System.lineSeparator()
        for (violation in violations)
            result += violation.toString() + System.lineSeparator()
        result += System.lineSeparator()+ "---------relations:"+ System.lineSeparator()
        for (relation in relations) {
            result += relation.table.columns
            result += relation.constraints
            result += System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

}