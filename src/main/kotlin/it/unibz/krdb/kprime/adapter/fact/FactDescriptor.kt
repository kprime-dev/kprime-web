package it.unibz.krdb.kprime.adapter.fact

import it.unibz.krdb.kprime.domain.cmd.CmdEnvironment
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table

class FactDescriptor {


    fun describe(contextEnv: CmdEnvironment, filter:String):String {
        return aboutAll(contextEnv)
    }

    private fun aboutAll(contextEnv: CmdEnvironment): String {
        return """
            trace ${contextEnv.currentTrace}
            trace file ${contextEnv.currentTraceFileName}
            data sources ${contextEnv.datasource?.name?:"-"}
            vocabularies ?
            terms number ?
            change-set size ${contextEnv.changeSet.size()}
            database table number ${contextEnv.database.schema.tables?.size?:"0"}
            database constraints number ${contextEnv.database.schema.constraints().size}
            database mappings number ${contextEnv.database.mappings?.size?:"0"}
        """.trimIndent()
    }

    fun describe(database: Database, filter:String):String {
        var facts = ""
        facts = aboutTables(database, filter, facts)
        facts = aboutMappings(database, facts)
        return facts
    }

companion object {

    private val lineBREAK = System.lineSeparator()

    fun describeTable(table: Table): String {
        var facts = ""
        facts += aboutColumns(table)
        return facts
    }

    private fun aboutColumns(table: Table, facts:String="" ): String {
        var facts1 = facts +"has:"+ lineBREAK
        for (col in table.columns) {
            val descType   = if (col.type.isNullOrEmpty())   "" else "type [${col.type}] "
            val descDbType = if (col.dbtype.isNullOrEmpty()) "" else "dbtype [${col.dbtype}] "
            val descDbName = if (col.dbname.isNullOrEmpty()) "" else "dbname [${col.dbname}] "
            val descLabels = if (col.labels.isNullOrEmpty()) "" else "labels [${col.labels}] "
            val descCardinality = if (col.cardinality.isNullOrEmpty()) "" else "card [${col.cardinality}] "
            val descRole   = if (col.role.isNullOrEmpty())   "" else "role [${col.role}] "
            facts1 += "${col.name} $descType$descDbType$descDbName$descLabels$descCardinality$descRole" + lineBREAK
        }
        return facts1
    }

    private fun aboutLabels(table: Table, facts: String): String {
        var facts1 = facts
        if (table.labelsAsString()!=null) {
            facts1 += " of  ${table.labelsAsString()} " + lineBREAK
        }
        return facts1
    }


}

    fun describeConstraints(database: Database, filter:String):String {
        var facts = ""
        facts = aboutConstraints(database, filter, facts)
        return facts
    }

    private fun aboutConstraints(database: Database, filter: String, facts: String): String {
        var facts1 = facts
        for (constraint in database.schema.constraintsByTable(filter)) {
            facts1 += constraint.toStringWithName() + lineBREAK
        }
        return facts1
    }

    private fun aboutMappings(database: Database, facts: String): String {
        var facts1 = facts
        for (mapping in database.mappings()) {
            facts1 += "${mapping.name} has:" + lineBREAK
            for (col in mapping.select.attributes) {
                facts1 += "  ${col.name} " + lineBREAK
            }
        }
        return facts1
    }

    private fun aboutTables(database: Database, filter: String, facts: String): String {
        var facts1 = facts
        for (table in database.schema.tables()) {
            val tableName = table.name.replace('_', ' ')
            if (tableName.startsWith(filter)) {
                facts1 += "$lineBREAK$tableName "
                facts1 = aboutParent(table, database, facts1)
                facts1 = aboutLabels(table, facts1)
                facts1 = aboutKeys(database, tableName, facts1)
                facts1 = aboutColumns(table, facts1)
            }
        }
        return facts1
    }

    private fun aboutKeys(database: Database, tableName: String, facts: String): String {
        var facts1 = facts
        for (key in database.schema.keysAll(tableName)) {
            facts1 += "key: ${key.left()} ${key.type}" + lineBREAK
        }
        return facts1
    }

    private fun aboutParent(table: Table, database: Database, facts: String): String {
        var facts1 = facts
        if (table.parent != null) {
            val parenttable = database.schema.table(table.parent!!)
            facts1 += " is ${table.parent} ${table.condition}" + lineBREAK
            if (parenttable != null) {
                facts1 = lineBREAK+" inherits "+aboutColumns(parenttable)
            }
        }
        return facts1
    }

}