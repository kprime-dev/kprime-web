package it.unibz.krdb.kprime.domain.cmd.expert

import it.unibz.krdb.kprime.domain.expert.Expert
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.isValidArgument
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdDropExpert : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "drop-expert"
    }

    override fun getCmdDescription(): String {
        return "drop an existing expert"
    }

    override fun getCmdUsage(): String {
        return getCmdName() +" <expert-name>"
    }

    override fun getCmdTopics(): String {
        return "write,expert"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()


        val tokens = command.split(" ")
        if (tokens.size != 2) return TraceCmdResult() failure getCmdUsage()

        val expertName = tokens[1]
        require(isValidArgument(expertName)){"Expert name invalid characters."}

        val expert : Expert = context.pool.expertService.getExpert(expertName)?: return TraceCmdResult() failure "Expert with '$expertName' not found."

        context.pool.expertService.dropExpert(expert)
        return TraceCmdResult() message "Droped expert $expertName ."
    }

}
