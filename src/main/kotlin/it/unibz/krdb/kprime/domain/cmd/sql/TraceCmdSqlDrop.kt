package it.unibz.krdb.kprime.domain.cmd.sql

class TraceCmdSqlDrop : TraceCmdSqlCreate() {
    override fun getCmdName(): String {
        return "drop"
    }

    override fun getCmdDescription(): String {
        return "Drop SQL command."
    }
}
