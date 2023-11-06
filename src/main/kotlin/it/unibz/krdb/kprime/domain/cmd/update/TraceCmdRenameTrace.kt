package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.isValidArgument
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdRenameTrace: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rename-trace"
    }

    override fun getCmdDescription(): String {
        return "Renames an exisiting trace in current project."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <trace-name> <new-trace-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        if (tokens.size < 2) return TraceCmdResult() failure getCmdUsage()

        val traceName = tokens[1]
        require(isValidArgument(traceName)){"Trace name invalid."}

//        val listTraces = it.unibz.krdb.kprime.domain.cmd.TraceCmd.listTraces(context)
//        if (!listTraces.any { it.equals(traceName) })
//        return TraceCmdResult() failure "Trace $traceName undefined." options listTraces

        val newTraceName = tokens[2]
        require(isValidArgument(newTraceName)){"Trace name invalid."}

//        if (listTraces.any { it.equals(newTraceName) })
//            return TraceCmdResult() failure "Trace $newTraceName already existing." options listTraces

        val tracesDir = context.pool.settingService.getTracesDir()
        File(tracesDir + traceName)
                .renameTo(File(tracesDir + newTraceName))

        return TraceCmdResult() message  "Ok trace renamed."
    }
}