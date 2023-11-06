package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.trace.TraceName
import org.jetbrains.kotlin.types.typeUtil.isNullabilityMismatch

object TraceCmdAddTable : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-table"
    }

    override fun getCmdDescription(): String {
        return "Add one table to current relational model."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<col-name>,...,<col-name>"
    }

    override fun getCmdTopics(): String {
        return listOf(TraceCmd.Topic.WRITE,
            TraceCmd.Topic.TABLE,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        println("add-table to database ID [${database.id}]")
        val tableName = command.trim().split(" ")[1]
        if (tableName.isEmpty()) return TraceCmdResult() failure "Required at least one arg Table name."
        if (database.schema.table(tableName) != null)  return TraceCmdResult() failure "There already exists a table with name ${tableName}."
        if (database.schema.relation(tableName) != null )   return TraceCmdResult() failure "There already exists a relation with name ${tableName}."
        context.env.currentElement = tableName
        database.schema.addTable(tableName)
        val tableOid = database.gid + database.schema.table(tableName)?.id?:"-"
        return TraceCmdResult() message "Add table executed." oid tableOid
    }

}