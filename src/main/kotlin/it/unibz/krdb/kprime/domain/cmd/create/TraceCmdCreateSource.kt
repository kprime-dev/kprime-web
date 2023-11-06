package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.datasource.DataSource
import java.util.*

object TraceCmdCreateSource : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "create-source"
    }

    override fun getCmdDescription(): String {
        return "create a new source H2"
    }

    override fun getCmdUsage(): String {
        return "usage: create-source <source-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,source"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val sourceName = command.split(" ")[1]

        val source = Source(
                UUID.randomUUID().toString(),
                "h2",
                sourceName,
                "org.h2.Driver",
                "jdbc:h2:file:~/$sourceName.db",
                "sa",
                "")
        context.pool.sourceService.addInstanceSource(source)

        val datasource = DataSource(
                name = source.name,
                type = source.type,
                path = source.location,
                driver = source.driver,
                user = source.user,
                pass = source.pass
        )
        context.env.datasource = datasource

        return TraceCmdResult() message "Created source ${sourceName} ."
    }

}
