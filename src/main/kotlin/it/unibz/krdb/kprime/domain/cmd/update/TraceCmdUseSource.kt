package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentSourceName
import it.unibz.krdb.kprime.domain.source.SourceName
import it.unibz.krdb.kprime.support.addIf

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

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val sourceNameValue = SourceName(args[ArgNames.SOURCE_NAME.name] as String).getValue()
        val sourceService = context.pool.sourceService
        context.env.database.source = sourceNameValue
        context.env.datasource = sourceService.newWorkingDataSourceOrH2(sourceNameValue)
        val contextName = context.env.prjContextName.value
        val sources = sourceService.readContextSources(contextName).toMutableList()
        val source = sourceService.getContextSourceByName(context.env.prjContextName,sourceNameValue)
            ?: return TraceCmdResult() failure "Source $sourceNameValue not found."
        sources.addIf(source) { it.name != source.name }
        sourceService.writeContextSources(contextName,sources)
        return TraceCmdResult() message "Set $sourceNameValue for current database."
    }

}

