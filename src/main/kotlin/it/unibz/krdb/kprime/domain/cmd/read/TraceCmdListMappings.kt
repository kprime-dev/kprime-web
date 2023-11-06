package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase

object TraceCmdListMappings : TraceCmd {
    override fun getCmdName(): String {
        return "mappings"
    }

    override fun getCmdDescription(): String {
        return "Show mappings from current database."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.MAPPING,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val database = context.env.database
        val mappings = database.mappings

        val mappingsDescriptor = mappings?.joinToString(System.lineSeparator()) { it.name }
            ?: (" 0 mappings. " + System.lineSeparator())
        val result = "Mappings:${System.lineSeparator()}$mappingsDescriptor" + System.lineSeparator()
        return TraceCmdResult() message result

    }

}
