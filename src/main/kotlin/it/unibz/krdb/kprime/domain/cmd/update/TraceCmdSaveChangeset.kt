package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File
import java.util.*

object TraceCmdSaveChangeset: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "save-changeset"
    }

    override fun getCmdDescription(): String {
        return "Save current changeset on file."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val changeset = context.env.changeSet
        changeset.id = UUID.randomUUID().toString()
//        val sourcesDir: String = context.settingService.getSourcesDir()
//        val sourceService: SourceService = context.sourceService
        val changesetContent = context.pool.dataService.prettyChangeSet(changeset)
        val traceFileName = context.getCurrentTraceDir() + "/" + changeset.id + "_cs.xml"
        context.env.currentTraceFileName = changeset.id
        File(traceFileName).writeText(changesetContent)
        return TraceCmdResult() message "Db ${changeset.id} saved."
    }
}