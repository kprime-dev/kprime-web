package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File
import java.util.*

object TraceCmdTraceGoal: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trace-goal"
    }

    override fun getCmdDescription(): String {
        return "Creates a new trace directory starting from a goal."
    }

    override fun getCmdUsage(): String {
        return "trace-goal <goal-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,goal"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val goalName = command.split(" ").drop(1).joinToString("_")

        if (context.env.currentTrace?.value.isNullOrEmpty()) {
            val settingService = context.pool.settingService
            val tracesDir = settingService.getWorkingDir() + SettingService.TRACES_DIR
            if (!File(tracesDir).isDirectory)
                File(tracesDir).mkdir()
            val nextSeed = Date().time
            val workingTraceDir = tracesDir + goalName + nextSeed
            File(workingTraceDir).mkdir()
            context.env.currentTrace = TraceName(goalName + nextSeed)
        }

        context.env.currentTraceFileName="Story_${goalName}.md"

        val storyname = "story_"+goalName
        if (storyname.isEmpty()) return TraceCmdResult() failure "no story name given."
        val storyText = """
            # Story GOAL $storyname
            
            ## Actors
            ## Sources
            ## Pros
            ## Cons
        """.trimIndent()

        val pathname = context.getCurrentTraceDir() + "Story_${storyname}.md"
        File(pathname).writeText(storyText)

        return TraceCmdResult() message "Goal $goalName traced."
    }

}
