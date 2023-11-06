package it.unibz.krdb.kprime.domain.cmd.sql

class TraceCmdSqlUpdate : TraceCmdSqlCreate() {
    override fun getCmdName(): String {
        return "update"
    }

    override fun getCmdDescription(): String {
        return "Update SQL command."
    }
}
