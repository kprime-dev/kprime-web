package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListAll : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "all"
    }

    override fun getCmdDescription(): String {
        return "List all about current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        var messageResult = ""
        messageResult += "Context name ${context.env.prjContextLocation}"+System.lineSeparator()
        messageResult += "Trace file ${context.env.currentTrace} ${context.env.currentTraceFileName}"+System.lineSeparator()
        messageResult += "ChangeSet size ${context.env.changeSet.size()}"+System.lineSeparator()
        messageResult += TraceCmdListTables.execute(context, command).message
        messageResult += System.lineSeparator()+"-------------"+System.lineSeparator()
        messageResult += TraceCmdListMappings.execute(context, command).message
        messageResult += System.lineSeparator()+"-------------"+System.lineSeparator()
        messageResult += TraceCmdListConstraints.execute(context, command).message
        return TraceCmdResult() message messageResult
    }

}
