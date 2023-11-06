package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdCurrentChangeset : TraceCmd {
    override fun getCmdName(): String {
        return "current-changeset"
    }

    override fun getCmdDescription(): String {
        return "Set current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [<trace-name>] <changet-filename>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        val (tmpTrace, databaseName) = traceAndFile(context.env.currentTrace, tokens)
        if (tmpTrace==null) return TraceCmdResult() failure "Trace undefined."
        val traceDir = context.pool.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + tmpTrace
        val sourceTraceFileName = "$traceDir/$databaseName"
        val xmlChangeSet = File(sourceTraceFileName).readText(Charsets.UTF_8)
        context.env.changeSet = context.pool.dataService.changeSetFromXml(xmlChangeSet)
        context.env.currentTrace = tmpTrace
        return TraceCmdResult() message "Setted $databaseName ." trace tmpTrace file xmlChangeSet
    }

    private fun traceAndFile(currentTrace: TraceName?, tokens: List<String>): Pair<TraceName?, String> {
        var tmpTrace = currentTrace
        val newDb: String
        if (tokens.size > 1) {
            tmpTrace = TraceName(tokens[0])
            newDb = tokens[1]
        } else {
            newDb = tokens[0]
        }
        return Pair(tmpTrace, newDb)
    }
}

