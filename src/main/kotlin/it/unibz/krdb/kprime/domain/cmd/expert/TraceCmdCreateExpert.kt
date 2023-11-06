package it.unibz.krdb.kprime.domain.cmd.expert

import it.unibz.krdb.kprime.domain.expert.Expert
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.isValidArgument
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdCreateExpert : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "create-expert"
    }

    override fun getCmdDescription(): String {
        return "create a new expert"
    }

    override fun getCmdUsage(): String {
        return getCmdName() +" <expert-name> <expert-url> [<expert-user> <expert-pass>]"
    }

    override fun getCmdTopics(): String {
        return "write,expert"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()


        val tokens = command.split(" ")
        if (tokens.size < 3) return TraceCmdResult() failure getCmdUsage()

        val expertName = tokens[1]
        require(isValidArgument(expertName)){"Expert name invalid characters."}
        val expertUrl = tokens[2]
        require(isValidArgument(expertUrl, it.unibz.krdb.kprime.domain.cmd.TraceCmd.URL_PATTERN)) {"Expert URL not valid." }
        val expertUser : String
        val expertPass : String
        if (tokens.size ==4) {
            expertUser = tokens[3]
            require(isValidArgument(expertUser)){"Expert user invalid characters."}
            expertPass = tokens[4]
            require(isValidArgument(expertPass)){"Expert pass invalid characters."}
        } else {
            expertUser = ""
            expertPass = ""
        }
        val newExpert = Expert(
                expertName,
                expertUrl,
                expertUser,
                expertPass)
        context.pool.expertService.addExpert(newExpert)
        return TraceCmdResult() message "Created expert $expertName ."
    }

}
