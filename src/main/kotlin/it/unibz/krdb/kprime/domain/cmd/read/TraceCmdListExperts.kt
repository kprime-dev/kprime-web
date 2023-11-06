package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListExperts: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "experts"
    }

    override fun getCmdDescription(): String {
        return "List all experts."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,expert"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val experts = context.pool.expertService.listExperts()
        var result = "Ok. ${experts.size} experts. "+System.lineSeparator()
        result += experts.joinToString(System.lineSeparator()) { " ${it.name} ${it.url}" }
        return TraceCmdResult() message result
    }
}