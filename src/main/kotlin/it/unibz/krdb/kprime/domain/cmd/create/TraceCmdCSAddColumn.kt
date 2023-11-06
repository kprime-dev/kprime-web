package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser
import unibz.cs.semint.kprime.domain.ddl.CreateColumn

object TraceCmdCSAddColumn: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-column"
    }

    override fun getCmdDescription(): String {
        return "Adds one column to a table via changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<table-col-name> [<table-type>][nullable][unique]"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val tableString = command.split(" ")[1]
        val table = SchemaCmdParser.parseTable(tableString)
        val createColumn = CreateColumn()
        createColumn.schema = ""
        createColumn.catalog = ""
        createColumn.columns.addAll(table.columns)
        changeSet.createColumn.add(createColumn)
        return TraceCmdResult() message "Add table to changeset executed."
    }
}