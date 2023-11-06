package it.unibz.krdb.kprime.domain.cmd.sql

class TraceCmdSqlAlter : TraceCmdSqlCreate() {
    override fun getCmdName(): String {
        return "alter"
    }

    override fun getCmdDescription(): String {
        return "Alter SQL command."
    }
}
