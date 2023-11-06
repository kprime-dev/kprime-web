package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.JsonService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdGetDatabase : TraceCmd {
    override fun getCmdName(): String {
        return "database"
    }

    override fun getCmdDescription(): String {
        return "Get current database details."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.DATABASE,
            TraceCmd.Topic.LOGICAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val database = context.env.database
        return TraceCmdResult()  message successMessage(context.pool.jsonService,database) payload database
    }

    private fun successMessage(jsonService: JsonService, database: Database): String {
        if (database.isEmpty()) return "Database is empty."
        return jsonService.toJson(database)
    }

}
