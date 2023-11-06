package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdAddKey : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-key"
    }

    override fun getCmdDescription(): String {
        return "Add a primary key to a relational table."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + "<table-name>:<col-names>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database: Database = context.env.database
        val keyName = command.split(" ")[1]
        database.schema.addKey(keyName)
        return TraceCmdResult() message "AddKey executed."
    }

}
