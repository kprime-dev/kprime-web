package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.expert.Expert
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd.Companion.isValidArgument
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdRenameExpert : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "rename-expert"
    }

    override fun getCmdDescription(): String {
        return "renames an existing expert"
    }

    override fun getCmdUsage(): String {
        return getCmdName() +" <expert-name> <new-expert-name>"
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
            require(isValidArgument(expertName)){"Expert name has invalid characters."}
            val expertNewName = tokens[2]
            require(isValidArgument(expertNewName)){"Expert new name has invalid characters."}
            val expert : Expert = context.pool.expertService.getExpert(expertName)?: return TraceCmdResult() failure "Expert with '$expertName' not found."
            context.pool.expertService.replace(expertName, expert.copy( name = expertNewName ))
            return TraceCmdResult() message "Renamed expert $expertName with new name $expertNewName."
    }

}
