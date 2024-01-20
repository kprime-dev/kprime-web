package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentSourceName
import it.unibz.krdb.kprime.domain.source.SourceName

object TraceCmdUseSource : TraceCmd {

    override fun getCmdName(): String {
        return "use-source"
    }

    override fun getCmdDescription(): String {
        return "Use a source as current."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.SOURCE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    internal enum class ArgNames {
        SOURCE_NAME;
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentSourceName(ArgNames.SOURCE_NAME.name, "Constraint as:") required true
        )
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {

        val sourceNameValue = SourceName(args[ArgNames.SOURCE_NAME.name] as String).getValue()
        val prjContextName = cmdContext.env.prjContextName

        val source = cmdContext.pool.sourceService.getContextDataSourceByName(prjContextName,sourceNameValue)
            ?: return TraceCmdResult() failure "Source $sourceNameValue not found."

        cmdContext.env.database.source = sourceNameValue
        cmdContext.env.datasource = source

        return TraceCmdResult() message "Set $sourceNameValue for current database."
    }

}

