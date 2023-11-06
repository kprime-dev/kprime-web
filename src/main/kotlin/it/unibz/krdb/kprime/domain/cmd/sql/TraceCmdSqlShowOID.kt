package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.*
import unibz.cs.semint.kprime.domain.db.Database

object TraceCmdSqlShowOID : TraceCmd {
    override fun getCmdName(): String {
        return "sql-show-oid"
    }

    override fun getCmdDescription(): String {
        return "Show SQL OID of a table."
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
        val result = computeOID(database,tableName)
        return TraceCmdResult() message result
    }

    internal fun computeOID(database: Database, tableName: String):String {
        val tables = database.schema.tables()
        val tablesToCompute =
            if (tableName != "*")tables.filter { it.name == tableName }
            else tables
        var result = """
            -- *
            -- * TABLE ${tableName} OID
            -- *
        """.trimIndent()
        for (table in tablesToCompute) {
            database.schema.keyPrimary(tableName).let { key ->
                println(key.toString())
                if (key != null) {
                    val keyCols = key.source.columns
                    val keyColsNames = keyCols.joinToString { it.name }
                    result += """
                                                    
                        CREATE TABLE ${tableName}OID AS SELECT ${keyColsNames} FROM ${tableName} WHERE ${keyColsNames} IS NOT NULL;
                        
                        ALTER TABLE ${tableName}OID ADD COLUMN OID int PRIMARY KEY AUTO_INCREMENT;
                         
                    """.trimIndent()
                    val attributes = table.columns.filter { !keyCols.contains(it) }
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        val attributeChained = if (i==attributes.size-1) attributes[0]
                            else attributes[i+1]
                        result += """
                            
                            -- * TABLE ${tableName}_OID_${attribute.name}
                            
                            CREATE TABLE ${tableName}_OID_${attribute.name} AS 
                                SELECT di.OID ,d.${attribute.name}
                                    FROM ${tableName} d LEFT JOIN ${tableName}OID di 
                                    ON d.${keyColsNames} = di.${keyColsNames}
                                    WHERE d.${attribute.name} IS NOT NULL ;

                            ALTER TABLE ${tableName}_OID_${attribute.name} ADD PRIMARY KEY(OID);

                                                                
                        """.trimIndent()
                    }

                    result += """
                        
                        -- * TABLE ${tableName} cross references integrity constraints
                        
                    """.trimIndent()
                    for (i  in 0..attributes.size-1) {
                        val attribute = attributes[i]
                        val attributeChained = if (i==attributes.size-1) attributes[0]
                        else attributes[i+1]
                        result += """

                            ALTER TABLE ${tableName}_OID_${attribute.name} ADD FOREIGN KEY (OID)
                            REFERENCES ${tableName}_OID_${attributeChained.name}(OID);

                            ALTER TABLE ${tableName}_OID_${attributeChained.name} ADD FOREIGN KEY (OID)
                            REFERENCES ${tableName}_OID_${attribute.name}(OID);
                            
                        """.trimIndent()
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
