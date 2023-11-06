package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd

import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdAddTrace: TraceCmd {
    override fun getCmdName(): String {
        return "add-trace"
    }

    override fun getCmdDescription(): String {
        return "Adds one trace folder."
    }

    override fun getCmdUsage(): String {
        return "usage: add-trace <new-trace-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val newTraceName = command.split(" ")[1]
        val tracesDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR
        if (!File(tracesDir).exists()) File(tracesDir).mkdir()
        val traceDir = tracesDir + newTraceName
        if (!File(traceDir).exists()) File(traceDir).mkdir()
        context.env.currentTrace = TraceName(traceDir)
        return TraceCmdResult() message "Trace $newTraceName created."
    }

}
