package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TraceCmdAddChangeset : TraceCmd {
    override fun getCmdName(): String {
        return "add-changeset"
    }

    override fun getCmdDescription(): String {
        return "Adds one changeset in memory."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [<trace-name>] <changeset-name-prefix>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val currentTrace = context.env.currentTrace
        val tokens = command.split(" ").drop(1)
        var tmpTrace = currentTrace
        val newCs: String
        if (tokens.size > 1) {
            tmpTrace = TraceName(tokens[0])
            newCs = tokens[1]
        } else {
            if (tmpTrace==null) return TraceCmdResult() failure "Trace undefined."
            newCs = tokens[0]
        }
        val traceDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + tmpTrace
        if (!File(traceDir).exists()) File(traceDir).mkdir()
        val tmpChangeset = ChangeSet()
        tmpChangeset.author = context.env.author
        tmpChangeset.id = UUID.randomUUID().toString()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        tmpChangeset.time = current.format(formatter)
        val revision = LocalDateTime.now().nano
        val csName = newCs + "_${revision}_cs.xml"
        val sourceContent = context.pool.dataService.prettyChangeSet(tmpChangeset)
        val sourceTraceFileName = "$traceDir/$csName"
        File(sourceTraceFileName).writeText(sourceContent)
        context.env.currentTrace = tmpTrace
        context.env.changeSet = tmpChangeset
        return TraceCmdResult() message "Changeset $csName created."
    }

}