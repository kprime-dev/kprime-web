package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TraceCmdTraceCs : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trace-changeset"
    }

    override fun getCmdDescription(): String {
        return "Trace current changeset into a file of the trace."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val sourceContent: String
        val changeset = context.env.changeSet
        if (context.env.currentTrace==null || context.env.currentTrace!!.isEmpty()) return TraceCmdResult() failure "no trace"
        val revision = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_nnnnnnnnnn"))
        val csName = "revision_${revision}_cs.xml"
        sourceContent = context.pool.dataService.prettyChangeSet(changeset)
        val sourceTraceFileName = context.getCurrentTraceDir() + csName
        File(sourceTraceFileName).writeText(sourceContent)
        return TraceCmdResult() message "CS traced."
    }

}
