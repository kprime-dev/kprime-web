package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import unibz.cs.semint.kprime.domain.datasource.DataSource

object TraceCmdCurrentSource : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "current-source"
    }

    override fun getCmdDescription(): String {
        return "set current source for this workspace."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE
        ).joinToString()
    }

    internal enum class ArgNames {
        newSource;
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.newSource.name, "New data source name to use as current.") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val newSourceName = args[ArgNames.newSource.name] as String? ?: ""
        if (newSourceName.isEmpty()) {
            val sourceName = if (context.env.database!=null) context.env.database.source
                                    else if (context.env.datasource!=null) context.env.datasource!!.name
                                    else  ""
            return TraceCmdResult() message "Current source: [${sourceName}] ."
        } else {
            val source = context.pool.sourceService.getInstanceSourceByName(newSourceName)
                ?: return TraceCmdResult() failure "No source $newSourceName"
            val datasource = DataSource(name = source.name,
                type = source.type,
                path = source.location,
                driver = source.driver,
                user = source.user,
                pass = source.pass
            )
            context.env.datasource = datasource
            context.env.database.source = newSourceName
            return TraceCmdResult() message "Setted ${newSourceName} ."
        }

    }

}
