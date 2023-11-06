package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdAddSpace: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-space"
    }

    override fun getCmdDescription(): String {
        return "Crete a new working space."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <space-name>"
    }

    override fun getCmdTopics(): String {
        return "write,space"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        val spaceName = tokens[1]
        val parentWorkingDir = File(context.pool.settingService.getWorkingDir()).parent
        val newWorkingDir = parentWorkingDir+"/$spaceName/"
        context.pool.settingService.newWorkingDir(newWorkingDir)
        context.pool.prjContextService.addProject(PrjContext(spaceName,newWorkingDir))
        return TraceCmdResult() message "Ok. Space $spaceName created."
    }
}