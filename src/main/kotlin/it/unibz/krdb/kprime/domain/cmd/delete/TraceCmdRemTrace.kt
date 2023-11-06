package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListTraces
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdRemTrace : TraceCmd {
    override fun getCmdName(): String {
        return "rem-trace"
    }

    override fun getCmdDescription(): String {
        return "Removes trace folder with all the content."
    }

    override fun getCmdUsage(): String {
        return "usage: rem-trace <trace-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tmpTrace = command.split(" ")[1]
        val traceDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + tmpTrace
        if (!File(traceDir).exists()) return helpWithFileNames(context)
        val deleted = File(traceDir).deleteRecursively()
        context.env.currentTrace = TraceName("")
        return if (deleted) TraceCmdResult() message "Trace $tmpTrace removed."
        else TraceCmdResult() failure "Trace $tmpTrace can't be removed."

    }

    private fun helpWithFileNames(context: CmdContext): TraceCmdResult {
       val listTraceNames = TraceCmdListTraces.execute(context, TraceCmdListTraces.getCmdName()).message.split(" ")
        return TraceCmdResult() failure "Trace to remove not found." options listTraceNames.map { name -> getCmdName() +" "+name }
    }
}
