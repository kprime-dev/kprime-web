package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
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

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val sourceName = SourceName(args[ArgNames.SOURCE_NAME.name] as String)
        context.env.database.source = sourceName.getValue()
        context.env.datasource = context.pool.sourceService.newWorkingDataSourceOrH2(sourceName.getValue())
        return TraceCmdResult() message "Set $sourceName for current database."
    }

}
