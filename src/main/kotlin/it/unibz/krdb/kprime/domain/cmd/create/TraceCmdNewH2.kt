package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

object TraceCmdNewH2: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "new-h2"
    }

    override fun getCmdDescription(): String {
        return "Creates a new H2 ram datasource."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,physical,datasource"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val datasource = DataSource("h2", "mem_db", "org.h2.Driver", "jdbc:h2:mem:mem_db", "sa", "")
        datasource.connection = DataSourceConnection("mem_db","sa","",true,true,false)
        context.env.datasource = datasource

        return TraceCmdResult() message "H2 context created."
    }
}