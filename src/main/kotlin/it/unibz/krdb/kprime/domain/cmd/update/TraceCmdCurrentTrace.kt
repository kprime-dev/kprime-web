package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListTraces
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdCurrentTrace : TraceCmd {
    override fun getCmdName(): String {
        return "current-trace"
    }

    override fun getCmdDescription(): String {
        return "Set current trace."
    }

    override fun getCmdUsage(): String {
        return "usage: current-trace <trace-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tmpTrace = TraceName(command.split(" ")[1])
        val traceDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + tmpTrace
        if (!File(traceDir).exists()) return helpWithFileNames(context)
        context.env.currentTrace = tmpTrace
        return TraceCmdResult() message "Trace ${tmpTrace.value} set." trace tmpTrace

    }

    private fun helpWithFileNames(context: CmdContext): TraceCmdResult {
       val listTraceNames = TraceCmdListTraces.execute(context, TraceCmdListTraces.getCmdName()).message.split(" ")
        return TraceCmdResult() failure "Trace to set not found." options listTraceNames.map { name -> getCmdName() +" "+name }
    }
}
