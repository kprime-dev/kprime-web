package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.ddl.DropMapping
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase

object TraceCmdCSAddMapping: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-mapping"
    }

    override fun getCmdDescription(): String {
        return "Adds one mapping to current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <mapping-name> <sql-view>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,table,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val tokens = command.split(" ")
        val mappingName = tokens[1]
        val sql = tokens.drop(2).joinToString(" ")
        val fromsql = UnSQLizeSelectUseCase().fromsql(mappingName, sql)
        if (context.env.database.mapping(mappingName)!=null)
            changeSet minus (DropMapping() withName mappingName)
        changeSet plus Mapping.fromQuery(fromsql)
        return TraceCmdResult() message "Add mapping to changeset executed."
    }
}