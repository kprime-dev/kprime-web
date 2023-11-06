package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdSetDbName : TraceCmd {
    override fun getCmdName(): String {
        return "set-db-name"
    }

    override fun getCmdDescription(): String {
        return "set name of current database"
    }

    override fun getCmdUsage(): String {
        return "set-db-name <new-database-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val newDatabaseName = command.split(" ")[1]
        require(TraceCmd.isValidArgument(newDatabaseName)) {"First argument new-db-name is not good [a-z A-Z 0-9 _]." }

        context.env.database.name = newDatabaseName
        return TraceCmdResult() message "Set $newDatabaseName name for current database."
    }

}
