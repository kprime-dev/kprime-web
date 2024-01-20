package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.source.SourceName

object TraceCmdRemSource: TraceCmd {
    override fun getCmdName(): String {
        return "rem-source"
    }

    override fun getCmdDescription(): String {
        return "Removes a context source."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.SOURCE,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { SOURCE_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.SOURCE_NAME.name,"name of the source",3,20) required true,
        )
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = cmdContext.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val contextName = cmdContext.env.prjContextName.value
        val remSourceName =  SourceName(args[ArgNames.SOURCE_NAME.name]  as String)
        cmdContext.pool.sourceService.remContextSource( contextName, remSourceName )
        return TraceCmdResult() message "Source ${remSourceName.value} removed."
    }
}