package it.unibz.krdb.kprime.domain.cmd.graalvm

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI

object TraceCmdRunJs: TraceCmd {
    override fun getCmdName(): String {
        return "js"
    }

    override fun getCmdDescription(): String {
        return "Run JS function."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.EXEC,
            TraceCmd.Topic.STORY,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { JS_FUN }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFreeText(ArgNames.JS_FUN.name, "JS function",pattern = "") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val jsFun =  args[ArgNames.JS_FUN.name] as String
        val prjContextLocation = context.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."


        context.pool.storyService.execJs(jsFun).fold(
                onSuccess = { jsFunResult -> return TraceCmdResult() message successMessage(context.pool.jsonService, jsFunResult)  payload jsFunResult},
                onFailure = { return TraceCmdResult() failure it.message.toString() }
            )
    }

    private fun successMessage(jsonService: JsonService, funResult: String): String {
        return jsonService.toJson(funResult)
    }
}