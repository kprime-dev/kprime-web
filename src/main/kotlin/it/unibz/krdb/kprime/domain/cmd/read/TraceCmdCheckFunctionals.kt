package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Constraint

object TraceCmdCheckFunctionals : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "check-functionals"
    }

    override fun getCmdDescription(): String {
        return "Checks functionals into current relational model."
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
        val dataSource: DataSource? = context.env.datasource
        val database = context.env.database
        if (dataSource == null) return TraceCmdResult() failure "No datasource."
        val functionals = database.schema.functionals()
        var queryResult:String = ""
        for (functional in functionals) {
            val checkQuery :String = getCheckFunctionalQuery(functional)
            queryResult += "Violations for ${functional.name}"+System.lineSeparator()
            val adapter = JdbcAdapter()
            queryResult += adapter.query(context.env.datasource!!, checkQuery, JdbcPrinter::printResultSet)
        }
        return TraceCmdResult() message queryResult
    }

    private fun getCheckFunctionalQuery(functional: Constraint): String {
        val table = functional.source.table
        val columnA = functional.source.columns[0].name
        val columnB= functional.target.columns[0].name
        val checkQuery = "select * from $table r1, $table r2 where r1.$columnB<>r2.$columnB and r1.$columnA=r2.$columnA"
        return checkQuery
    }

}


