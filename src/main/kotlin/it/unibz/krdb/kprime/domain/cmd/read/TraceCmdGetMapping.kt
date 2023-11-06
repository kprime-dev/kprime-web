package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase

object TraceCmdGetMapping : TraceCmd {
    override fun getCmdName(): String {
        return "mapping"
    }

    override fun getCmdDescription(): String {
        return "Show one mapping from current database."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.MAPPING,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    private enum class ArgNames { MAPPING_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.MAPPING_NAME.name, "Mapping name") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val database = context.env.database

        val mappingName =  args[ArgNames.MAPPING_NAME.name] as String
        val mappingDescriptor = database.mapping(mappingName).let { mapping ->
            SQLizeSelectUseCase().sqlize(mapping?: Query()) +
                    System.lineSeparator() + mapping?.options?.joinToString()
        }
        val result = "Mapping $mappingName: $mappingDescriptor" + System.lineSeparator()
        return TraceCmdResult() message result
    }

}
