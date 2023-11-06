package it.unibz.krdb.kprime.domain.cmd.sql

import it.unibz.krdb.kprime.domain.cmd.*
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.dql.Attribute
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase

object TraceCmdSqlSelect : TraceCmd {
    override fun getCmdName(): String {
        return "select"
    }

    override fun getCmdDescription(): String {
        return "Select SQL command."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <sql-args>"
    }

    override fun getCmdTopics(): String {
        return "read,sql,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database

        val (sqlCommandLines,optionals) = TraceCmd.separateArgsOptionals(command)

        val projectName = context.env.prjContextName
        val prjContext =
         context.pool.prjContextService.projectByName(projectName.value)
            ?: return TraceCmdResult() failure "Context ${projectName.value} not found."

        var sqlCommandWithOptions = sqlCommandViaMappingName(database, sqlCommandLines.joinToString(" "))
//            sqlCommand = sqlCommandViaTableName(database, tokens, sqlCommand)
//            sqlCommand = sqlCommandViaTableAlias(database, tokens, sqlCommand)
        sqlCommandWithOptions = sqlCommandViaEnvelope(context.envelope,sqlCommandWithOptions)
//            sqlCommandWithOptions = sqlCommandWithDefaultLimit(sqlCommandWithOptions)

        val datasourceNameFromCmd = optionals["source"]?:""
        val datasourceNameFromDb = database.source
        val datasourceName = if (datasourceNameFromCmd.isNotEmpty()) datasourceNameFromCmd
            else datasourceNameFromDb
        println("....................")
        println("sqlCommand [$sqlCommandWithOptions]")
        println("datasourceNameFromCmd [$datasourceNameFromCmd]")
        println("datasourceNameFromDb [$datasourceNameFromDb]")
        println("^^^^^^^^^^^^^^^^^^^^")
        val workingDataSource = context.pool.dataService.extractDataSource(prjContext, datasourceName)
                                            ?: context.env.datasource
                                            ?: context.pool.sourceService.newWorkingDataSourceOrH2(database.source)
        val result = context.pool.dataService.query(workingDataSource, sqlCommandWithOptions)
        return TraceCmdResult() message result
    }

    // SELECT * FROM datasource:schema.table
    fun extractDatasourceNameFromCmd(sqlCommand: String):Pair<String,String> {
        val tokens = sqlCommand.split(" ")
        val newTokens = mutableListOf<String>()
        var datasourceName = ""
        var splitted = false
        for (i in 1..tokens.size) {
//            println(" check "+tokens[i-1])
            if (tokens[i-1].uppercase().trim()=="FROM" && tokens[i].contains(':')) {
//                println(" split "+tokens[i-1])
                    datasourceName = tokens[i].trim().substringBefore(":")
                    newTokens.add("from")
                    newTokens.add(tokens[i].trim().substringAfter(":"))
                    splitted = true
            } else {
                if (!splitted) {
//                    println(" add " + tokens[i - 1])
                    newTokens.add(tokens[i - 1])
                }
                splitted = false
            }

        }
        return Pair(newTokens.joinToString(" "),datasourceName)
    }

    internal fun sqlCommandViaEnvelope(evelope: CmdEnvelope, sqlCommandWithOptions: String): String {
        return if (evelope.cmdPayload.isNotEmpty()) {
            "SELECT "+evelope.cmdPayload.joinToString(" ")
        } else sqlCommandWithOptions
    }

    private fun sqlCommandViaTableAlias(
        database: Database,
        tokens: List<String>,
        sqlCommand: String
    ): String {
        var sqlCommand1 = sqlCommand
        val tableAlias = database.schema.tables().firstOrNull { t -> t.hasLabel("alias_${tokens[1]}") }
        if (tableAlias != null) sqlCommand1 = "select * from " + tableAlias.name
        return sqlCommand1
    }

    private fun sqlCommandViaTableName(
        database: Database,
        tokens: List<String>,
        sqlCommand: String
    ): String {
        var sqlCommand1 = sqlCommand
        val table = database.schema.table(tokens[1])
        if (table != null) {
            val colNames = table.columns
                .filter { !it.hasLabel("hidden") }
                .map { c -> c.dbname + " as " + c.name }
                .joinToString(",")
            sqlCommand1 = "select $colNames from " + (tokens.drop(1).joinToString(" "))
        }
        return sqlCommand1
    }

    internal fun sqlCommandViaMappingName(
        database: Database,
        sqlCommand: String
    ): String {
        val tokens = sqlCommand.split(" ")
        if (tokens.size<2) return sqlCommand
        val mapping = database.mapping(tokens[1]) ?: return sqlCommand
        val mappingExploded = explodeMapping(database, mapping)
        var sqlCommand1 = SQLizeSelectUseCase().sqlize(mappingExploded)
        val options = mapping.options
        if (options != null) {
            sqlCommand1 += " " + options.joinToString(" ")
        }
        return sqlCommand1
    }

    private fun explodeMapping(database: Database, mapping: Mapping): Mapping {
        var leafMapping = database.mapping(mapping.select.from.tableName)
        if (leafMapping==null)  return mapping
        val explodedMapping = Mapping()
        while (leafMapping!=null) {
            explodedMapping.name = mapping.name
            explodedMapping.union = mapping.union
            explodedMapping.minus = mapping.minus
            explodedMapping.options = mapping.options
            explodedMapping.select = mapping.select
            if (mapping.select.attributes.equals(listOf(Attribute("*"))))
                explodedMapping.select.attributes = leafMapping.select.attributes
            else
                explodedMapping.select.attributes = leafMapping.select.attributes.intersect(mapping.select.attributes).toMutableList()
            explodedMapping.select.from.tableName = leafMapping.select.from.tableName
            explodedMapping.select.where.condition = mapping.select.where.condition
            if (leafMapping.select.where.condition.isNotEmpty())
                explodedMapping.select.where.condition += " AND " +leafMapping.select.where.condition
            leafMapping = database.mapping(explodedMapping.select.from.tableName)
        }
        return explodedMapping
    }

    internal fun sqlCommandWithDefaultLimit(sqlCommand: String): String {
        var sqlCommand1 = sqlCommand
        if (!sqlCommand1.lowercase().contains(" limit ")) {
            sqlCommand1 += " LIMIT 10"
        }
        return sqlCommand1
    }

}
