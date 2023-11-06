package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdGetActor: TraceCmd {
    override fun getCmdName(): String {
        return "actor"
    }

    override fun getCmdDescription(): String {
        return "Get actors details."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.ACTOR,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { ACTOR_NAME }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.ACTOR_NAME.name, "Actor name") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val actorName =  args[ArgNames.ACTOR_NAME.name] as String
        val prjContextLocation = context.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        context.pool.actorService.getByName(prjContextLocation.value,actorName).fold(
                onSuccess = { actor -> return TraceCmdResult() message successMessage(context.pool.jsonService,actor) payload actor},
                onFailure = { return TraceCmdResult() failure it.message.toString() }
            )
    }

    private fun successMessage(jsonService: JsonService, actor: Actor): String {
        return jsonService.toJson(actor)
    }
}