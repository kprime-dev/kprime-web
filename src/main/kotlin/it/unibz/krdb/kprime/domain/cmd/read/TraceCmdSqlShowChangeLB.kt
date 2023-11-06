package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.service.LiquibaseSQLizeAdapter
import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

object TraceCmdSqlShowChangeLB : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "sql-show-changeset-lb"
    }

    override fun getCmdDescription(): String {
        return "Expose LiquiBase SQL changeset commands."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,sql"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet: ChangeSet = context.env.changeSet
        var result = ""
        result += LiquibaseSQLizeAdapter().sqlize(DatabaseTrademark.H2, changeSet).joinToString(System.lineSeparator())
        return TraceCmdResult() message result
    }

}
