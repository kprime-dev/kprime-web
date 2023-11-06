package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase

object TraceCmdSqlShowViews : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "sql-show-mappings"
    }

    override fun getCmdDescription(): String {
        return "Expose SQL CREATE VIEWS commands from current database mappings."
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
        val database = context.env.database
        val createViewCommands = SQLizeCreateUseCase().createViewCommands(database)
        var result = ""
        for (cmd in createViewCommands) {
            result += "---------"+ System.lineSeparator()
            result += cmd + System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

}
