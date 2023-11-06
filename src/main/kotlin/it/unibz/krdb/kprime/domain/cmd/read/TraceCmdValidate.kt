package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.update.TraceCmdSetDbName
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdValidate: it.unibz.krdb.kprime.domain.cmd.TraceCmd {

    override fun getCmdName(): String {
        return "validate"
    }

    override fun getCmdDescription(): String {
        return "Validate current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,assist"
    }

    private val BREAK = System.lineSeparator()

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        var validationMessage = "Validation result:$BREAK"
        validationMessage += checkDatabase(context)
        validationMessage += checkTables(context)
        return TraceCmdResult() message validationMessage
    }

    private fun checkDatabase(context: CmdContext): String {
        if (context.env.database.name.isEmpty()) return "No database name, use '${TraceCmdSetDbName.getCmdName()}'." + BREAK
        if (context.env.database.schema.id.isEmpty()) return "No database schema ID.$BREAK"
        return "Database OK.$BREAK"
    }

    private fun checkTables(context: CmdContext): String {
        val tables = context.env.database.schema.tables
        if (tables==null || tables.isEmpty()) return "No tables.$BREAK"
        for (table in tables) {
            if (table.id.isEmpty()) return "Table ${table.name} with no ID." + BREAK
            if (table.name.isEmpty()) return "Tables ID=${table.id} ha no name." + BREAK
            if (table.columns.isNullOrEmpty()) "Tables ID=${table.id} ha no columns." + BREAK
        }
        return "${tables.size} Tables OK." + BREAK
    }

    // TODO checkConstraints
    private fun checkConstraints(context: CmdContext): String {
        val constraints = context.env.database.schema.constraints
        if (constraints==null || constraints.isEmpty()) return "No constraints.$BREAK"
        for (constraint in constraints) {
            if (constraint.id.isEmpty()) return "Constraint ${constraint.name} with no ID." + BREAK
            if (constraint.name.isEmpty()) return "Constraint ID=${constraint.id} ha no name." + BREAK
        }
        return "${constraints.size} Constraints OK." + BREAK
    }

}