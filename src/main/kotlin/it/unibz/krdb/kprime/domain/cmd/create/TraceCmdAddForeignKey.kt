package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddForeignKey : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-foreign-key"
    }

    override fun getCmdDescription(): String {
        return "Add one foreign ket to actual relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdUsage() + " <source-tab-name>:<attr-names> --> <target-tab-name>:<attr-names>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        database.schema.addForeignKey(commandWithoutPrefix)
        return TraceCmdResult() message "AddForeignKey executed."
    }

}
