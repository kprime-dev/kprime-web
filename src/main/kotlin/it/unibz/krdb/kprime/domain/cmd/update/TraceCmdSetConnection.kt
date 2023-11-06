package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

object TraceCmdSetConnection: it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    override fun getCmdName(): String {
        return "set-connection"
    }

    override fun getCmdDescription(): String {
        return """
            e.g. ${getCmdName()} conn1 autocommit-true 
            e.g. ${getCmdName()} conn1 autocommit-true closed-true 
        """.trimIndent()
    }

    override fun getCmdUsage(): String {
        return getCmdName() + "  <connection-name> [autocommit:true | false] [closed:true | false]"
    }

    override fun getCmdTopics(): String {
        return "config,sql,database,connection"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        if (tokens.size < 3) {
            return TraceCmdResult() failure getCmdUsage()
        }
        val connectionName = tokens[1]
        if (!it.unibz.krdb.kprime.domain.cmd.TraceCmd.isValidArgument(connectionName))
            return TraceCmdResult() failure "Connection name syntax error."

        var datasource = context.env.datasource
        if  (datasource == null)
            datasource = DataSource("h2",
                    "mem_db",
                    "org.h2.Driver",
                    "jdbc:h2:mem:mem_db",
                    "sa", "")

        var resultMessage = "Connection set "
        if  (datasource.connection == null) {
            datasource.connection = DataSourceConnection(connectionName,
                    "sa","",
                    autocommit = true,
                    commited = true,
                    closed = false)
        } else if  (datasource.connection!!.id != connectionName) {
            datasource.connection =  DataSourceConnection(connectionName,
                    "sa","",
                    autocommit = true,
                    commited = true,
                    closed = false)
        }
        val connectionDescriptor = datasource.connection!!
        if (command.contains("autocommit:true")) {
            connectionDescriptor.autocommit = true
            resultMessage+=" autocommit-true"
        }
        if (command.contains("autocommit:false")) {
            connectionDescriptor.autocommit = false
            resultMessage+=" autocommit-false"
        }
        if (command.contains("closed:true")) {
            connectionDescriptor.closed = true
            resultMessage+=" closed-true"
        }
        if (command.contains("closed:false")) {
            connectionDescriptor.closed = true
            resultMessage+=" closed-false"
        }
        return TraceCmdResult() message resultMessage
    }
}