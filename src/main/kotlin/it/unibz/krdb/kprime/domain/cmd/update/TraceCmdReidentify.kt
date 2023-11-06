package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.util.*

object TraceCmdReidentify: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "reidentify"
    }

    override fun getCmdDescription(): String {
        return "Reset ID values for current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val db = context.env.database
        db.id = UUID.randomUUID().toString()
        db.schema.id="s1"
        var tid = 0
        for (table in db.schema.tables()) {
            tid++; table.id = "t$tid"
            var aid = 0
            for (column in table.columns) {
                aid++; column.id = "t$tid-a$aid";
            }
        }
        var cid = 0
        for (constraint in db.schema.constraints()) {
            cid++; constraint.id = "c$cid";
        }
        var mid = 0
        for (mapping in db.mappings()) {
            mid++; mapping.id = "m$mid";
        }
        return TraceCmdResult() message "Database reindexed."
    }
}