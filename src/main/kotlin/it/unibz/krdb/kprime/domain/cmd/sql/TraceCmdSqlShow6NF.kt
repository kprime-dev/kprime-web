package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.*
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdSqlShow6NF : TraceCmd {
    override fun getCmdName(): String {
        return "sql-show-6nf"
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
        var result = """
            -- *
            -- * TABLE ${tableName}
            -- *
        """.trimIndent()
        for (table in tablesToCompute) {
            database.schema.keyPrimary(tableName).let { key ->
                println(key.toString())
                if (key != null) {
                    val keyCols = key.source.columns
                    val keyColsNames = keyCols.joinToString { it.name }
                    val attributes = table.columns.filter { !keyCols.contains(it) }
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
//                        val attributeChained = if (i==attributes.size-1) attributes[0]
//                            else attributes[i+1]
                        result += """
                            
                            -- * TABLE ${tableName}_6NF_id_${attribute.name}
                            
                            CREATE TABLE ${tableName}_6NF_id_${attribute.name}
                            AS SELECT t.${keyColsNames}, t.${attribute.name}
                            FROM ${tableName} t;
                            
                            ALTER TABLE ${tableName}_6NF_id_${attribute.name} ADD PRIMARY KEY(${keyColsNames});
                            
                        """.trimIndent()

                    }
                    result += """

                        -- * cross references integrity constraints

                    """.trimIndent()

                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        for (j  in 0..attributes.size-1) {
                            if (i!=j) {
                                result += """

                            ALTER TABLE ${tableName}_6NF_id_${attribute.name} ADD FOREIGN KEY (${keyColsNames})
                            REFERENCES ${tableName}_6NF_id_${attributes[j].name}(${keyColsNames});

                                    """.trimIndent()
                            }
                        }
                    }

                    val fkeysTarget = database.schema.foreignsWithTarget(tableName)
                    if (fkeysTarget.isNotEmpty()) {
                        result += """
                        
                        -- * updates ${fkeysTarget.size} TARGET foreign key constraints 
                        
                        """.trimIndent()
                        for (fkey in fkeysTarget) {
                            result += """
                            
                            -- * updates TARGET foreign key constraints ${fkey}
                            
                            """.trimIndent()
                        }
                    }
                    val fkeysSource = database.schema.foreignsWithSource(tableName)
                    if (fkeysSource.isNotEmpty()) {
                            result += """
                        
                        -- * updates ${fkeysTarget.size} SOURCE foreign key constraints 
                        
                        """.trimIndent()
                        for (fkey in fkeysSource) {
                            result += """
                            
                            -- * updates SOURCE foreign key constraints ${fkey}
                            
                            """.trimIndent()
                        }
                    }
                }
            }
        }
        return result
    }

    private fun sqlCommandViaEnvelope(evelope: CmdEnvelope, sqlCommandWithOptions: String): String {
        return if (evelope.cmdPayload.isNotEmpty()) {
            "SELECT "+evelope.cmdPayload.joinToString(" ")
        } else sqlCommandWithOptions
    }

}
