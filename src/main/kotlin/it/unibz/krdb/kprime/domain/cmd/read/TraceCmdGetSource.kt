package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.trace.ScopedName

object TraceCmdGetSource : TraceCmd {
    override fun getCmdName(): String {
        return "source"
    }

    override fun getCmdDescription(): String {
        return "Get one source details."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.SOURCE,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { SCOPED_SOURCE_ID }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.SCOPED_SOURCE_ID.name, "Source ID") required true
        )
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = cmdContext.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val scopedSourceId =  args[ArgNames.SCOPED_SOURCE_ID.name] as String

        if (scopedSourceId=="_") {
            val datasource = cmdContext.env.datasource ?:
                return TraceCmdResult() failure "Datasource not set in context."
            return TraceCmdResult()  message cmdContext.pool.jsonService.toJson(datasource)
        }

        //val sources = context.pool.sourceService.readInstanceSources()
        val contextName = cmdContext.env.prjContextName
        return cmdContext.pool.sourceService.getSourceByScopedId(contextName, ScopedName(scopedSourceId) ).fold(
            onSuccess = { source -> TraceCmdResult()  message successMessage(cmdContext.pool.jsonService,source) payload source },
            onFailure = { error -> TraceCmdResult() failure errorMessage(error) }
        )
    }

    private fun successMessage(jsonService: JsonService, source: Source): String {
        return jsonService.toJson(source)
    }

    private fun errorMessage(error: Throwable): String {
        return error.message?: "Error on finding source [${error.message}]."
    }

    private fun successMessage(sources: List<Source>): String {
        var result = "Sources:"+System.lineSeparator()
        for (source in sources) { result += source.name + ":" + source.driver + System.lineSeparator() }
        return result
    }


}
