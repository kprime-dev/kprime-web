package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListActors: TraceCmd {
    override fun getCmdName(): String {
        return "actors"
    }

    override fun getCmdDescription(): String {
        return "List actors for this context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.ACTOR,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = context.env.prjContextLocation
        val allActors = context.pool.actorService.allActors(prjContextLocation.value)
            .getOrElse { return TraceCmdResult() failure it.message.toString() }
        return TraceCmdResult() payload allActors message successMessage(allActors)
    }

    private fun successMessage(allActors: List<Actor>) =
        "Actors ${allActors.size}"+ System.lineSeparator() +
        allActors.joinToString(System.lineSeparator()) { "(${it.id}) ${it.name} [${it.role}]" }
}