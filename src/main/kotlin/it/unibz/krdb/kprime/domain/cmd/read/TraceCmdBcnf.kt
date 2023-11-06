package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext

import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdBcnf : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "bcnf"
    }

    override fun getCmdDescription(): String {
        return "Check if current relational model is in Boyce-Codd normal-form."
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
        val relations = database.schema.decomposeBCNF()
        val violations = database.schema.violationsBCNF()
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


