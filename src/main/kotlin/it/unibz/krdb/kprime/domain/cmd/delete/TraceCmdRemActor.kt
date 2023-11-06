package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdRemActor: TraceCmd {
    override fun getCmdName(): String {
        return "rem-actor"
    }

    override fun getCmdDescription(): String {
        return "Remove one actor."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
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
        context.pool.actorService.removeActor(prjContextLocation) { it.name == actorName }.fold(
                onSuccess = { successMessage -> return TraceCmdResult() message successMessage},
                onFailure = { return TraceCmdResult() failure it.message.toString() }
            )
    }

}