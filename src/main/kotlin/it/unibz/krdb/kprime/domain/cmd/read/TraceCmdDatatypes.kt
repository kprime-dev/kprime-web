package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdDatatypes: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "datatypes"
    }

    override fun getCmdTopics(): String {
        return "read,logic,database"
    }

    override fun getCmdDescription(): String {
        return "Shows the list of source data types available."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val sourceName = context.env.database.source
        val source = context.pool.sourceService.getInstanceSourceByName(sourceName)

        var listOfDatatypes = "No datatypes available."
        if (source!=null && source.driver.contains("h2"))
            listOfDatatypes = context.pool.termService.getH2Terms()
                    .joinToString(System.lineSeparator()) { "%-10s\t%s".format(it.name, it.description) }
        return TraceCmdResult() message listOfDatatypes

    }

}