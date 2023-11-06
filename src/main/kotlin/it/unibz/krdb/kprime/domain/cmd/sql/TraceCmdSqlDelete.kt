package it.unibz.krdb.kprime.domain.cmd.sql

class TraceCmdSqlDelete : TraceCmdSqlCreate() {
    override fun getCmdName(): String {
        return "delete"
    }

    override fun getCmdDescription(): String {
        return "Delete SQL command."
    }
}
