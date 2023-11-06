package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.source.Source

object TraceCmdListSources : TraceCmd {
    override fun getCmdName(): String {
        return "sources"
    }

    override fun getCmdDescription(): String {
        return "List sources in context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.SOURCE,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = cmdContext.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val prjContextName = cmdContext.env.prjContextName
        val instanceSources = cmdContext.pool.sourceService.readInstanceSources()
        val contextSources = cmdContext.pool.sourceService.readContextSources(prjContextName.value)
        return TraceCmdResult()  message successMessage(instanceSources,contextSources)
    }

    private fun successMessage(instanceSources: List<Source>,contextSources: List<Source>): String {
        var result = "Sources:"+System.lineSeparator()
        for (source in instanceSources) { result += "(I${source.id}) ${source.name}: ${source.type}" + System.lineSeparator() }
        for (source in contextSources)  { result += "(C${source.id}) ${source.name}: ${source.type}" + System.lineSeparator() }
        return result
    }


}
