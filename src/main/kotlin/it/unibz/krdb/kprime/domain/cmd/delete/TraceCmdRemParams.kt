package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdRemParams: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rem-params"
    }

    override fun getCmdDescription(): String {
        return "Removes all transformer parameters."
    }

    override fun getCmdUsage(): String {
        return "rem-rapams"
    }

    override fun getCmdTopics(): String {
        return "write,logical,transformer"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        context.env.currentParams.clear()
        return TraceCmdResult() message "Pramateres cleared."
    }

}

