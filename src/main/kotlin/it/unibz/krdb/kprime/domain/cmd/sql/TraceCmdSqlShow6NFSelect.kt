package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.*
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdSqlShow6NFSelect : TraceCmd {
    override fun getCmdName(): String {
        return "sql-show-6nf-select"
    }

    override fun getCmdDescription(): String {
        return "Show SQL 6NF of a table."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,sql,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database

        val sqlCommandWithOptions = sqlCommandViaEnvelope(context.envelope,command)
        val (args,optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        val tableName = if (args.size<=1) "" else args[1]
        val datasourceNameFromCmd = optionals["source"]?:""
        if (tableName.isEmpty()) return TraceCmdResult() failure "Table name is required."
        println("....................")
        println("tableName [$tableName]")
        println("datasourceNameFromCmd [$datasourceNameFromCmd]")
        println("^^^^^^^^^^^^^^^^^^^^")
        val result = compute6NF(database,tableName)
        return TraceCmdResult() message result
    }

    internal fun compute6NF(database: Database, tableName: String):String {
        val tables = database.schema.tables()
        val tablesToCompute =
            if (tableName != "*")tables.filter { it.name == tableName }
            else tables
        var result = ""
        for (table in tablesToCompute) {
            var keyResult = ""
            var selectResult = ""
            var fromResult = ""
            var whereResult = ""
            database.schema.keyPrimary(tableName).let { key ->
                println(key.toString())
                if (key != null) {
                    val keyCols = key.source.columns
                    val keyColsNames = keyCols.joinToString { it.name }
                    val attributes = table.columns.filter { !keyCols.contains(it) }
                    keyResult = "\n${tableName}_6NF_id_${attributes[0].name}.${keyColsNames}"
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        selectResult += "\n${tableName}_6NF_id_${attribute.name}.${attribute.name}"
                        if (i!=attributes.size-1)
                            selectResult += ","

                    }
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        fromResult += "\n${tableName}_6NF_id_${attribute.name}"
                        if (i!=attributes.size-1)
                            fromResult += ","

                    }
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        val attributeChained = if (i==attributes.size-1) attributes[0]
                            else attributes[i+1]
                        whereResult += " \n       ${tableName}_6NF_id_${attribute.name}.${keyColsNames} = ${tableName}_6NF_id_${attributeChained.name}.${keyColsNames}"
                        if (i!=attributes.size-1)
                            whereResult += " AND"

                    }


                }
            }
            result += """
            -- *
            -- * TABLE ${tableName}
            -- *
            CREATE VIEW ${tableName}_6NF AS 
                SELECT ${keyResult},${selectResult}
                FROM ${fromResult}
                WHERE ${whereResult}
            """.trimIndent()
        }

        return result
    }

    private fun sqlCommandViaEnvelope(evelope: CmdEnvelope, sqlCommandWithOptions: String): String {
        return if (evelope.cmdPayload.isNotEmpty()) {
            "SELECT "+evelope.cmdPayload.joinToString(" ")
        } else sqlCommandWithOptions
    }

}
