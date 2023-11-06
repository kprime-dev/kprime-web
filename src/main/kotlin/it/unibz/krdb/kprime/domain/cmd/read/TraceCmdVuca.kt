package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.StatService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdVuca : TraceCmd {
    override fun getCmdName(): String {
        return "vuca"
    }

    override fun getCmdDescription(): String {
        return "Get VUCA statistics."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.STATISTICAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val vucaStats = context.pool.statService.computeVUCA(contextLocation.value)
        return TraceCmdResult()  message successMessage(context.pool.jsonService,vucaStats) payload vucaStats
    }

    private fun successMessage(jsonService: JsonService, vucaStats: StatService.Vuca): String {
        return jsonService.toJson(vucaStats)
    }

}
