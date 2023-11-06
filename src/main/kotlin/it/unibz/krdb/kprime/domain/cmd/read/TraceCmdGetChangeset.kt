package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.source.Source
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

object TraceCmdGetChangeset : TraceCmd {
    override fun getCmdName(): String {
        return "changeset"
    }

    override fun getCmdDescription(): String {
        return "Get current changeset details."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.CHANGESET,
            TraceCmd.Topic.LOGICAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val changeset = context.env.changeSet
        return TraceCmdResult()  message successMessage(context.pool.jsonService,changeset) payload changeset
    }

    private fun successMessage(jsonService: JsonService, changeset: ChangeSet?): String {
        if (changeset==null) return "Changeset is empty."
        // return changeset.commands?.joinToString(separator = System.lineSeparator()) ?: ""
        return jsonService.toJson(changeset)
    }

}
