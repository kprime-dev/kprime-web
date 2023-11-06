package it.unibz.krdb.kprime.domain.cmd.sql

class TraceCmdSqlInsert : TraceCmdSqlCreate() {
    override fun getCmdName(): String {
        return "insert"
    }

    override fun getCmdDescription(): String {
        return "Insert SQL command."
    }
}
