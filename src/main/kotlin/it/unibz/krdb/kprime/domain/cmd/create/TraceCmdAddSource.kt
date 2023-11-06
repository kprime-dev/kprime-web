package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdAddSource: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-source"
    }

    override fun getCmdDescription(): String {
        return "Creates a new H2 source."
    }

    override fun getCmdUsage(): String {
        return getCmdName()+" <source-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,source"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val newSourceName = command.split(" ")[1]
        val source = Source(
                id = newSourceName,
        type = "h2",
        name= newSourceName,
        driver= "org.h2.Driver",
        location="jdbc:h2:file:~/data/${newSourceName}.db",
        user="sa",
        pass="")
        context.pool.sourceService.addInstanceSource(source)
        return TraceCmdResult() message "Source created."
    }
}