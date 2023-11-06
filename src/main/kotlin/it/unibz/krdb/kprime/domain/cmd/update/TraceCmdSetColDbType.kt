package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdSetColDbType: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "set-col-dbtype"
    }

    override fun getCmdDescription(): String {
        return "Sets the data type of column-name of table-name"
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name> <col-name> <col-db-type>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,database"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        var tokens = command.split(" ")
        if (tokens.size<4) return TraceCmdResult() failure getCmdUsage()
        var tableName = tokens[1]
        require(it.unibz.krdb.kprime.domain.cmd.TraceCmd.isValidArgument(tableName)) {"First argument table-name is not good [a-z A-Z 0-9 _]." }

        var colName = tokens[2]
        require(it.unibz.krdb.kprime.domain.cmd.TraceCmd.isValidArgument(colName)) {"Second argument col-name is not good [a-z A-Z 0-9 _]." }

        var colDbType = tokens[3]
        require(it.unibz.krdb.kprime.domain.cmd.TraceCmd.isValidArgument(colDbType, "a-zA-Z0-9_()")) {"col-db-type is not good [a-z A-Z 0-9 _ ()]." }

        val col = context.env.database.schema.table(tableName)?.columns?.filter { col -> col.name.equals(colName) }?.first()
        if (col==null) return TraceCmdResult() failure "No col $tableName $colName found in current database."
        col.dbtype = colDbType

        return TraceCmdResult() message "Ok. for table $tableName with col $colName type changed to $colDbType ."
    }
}