package it.unibz.krdb.kprime.domain

data class Eventlog (
    val table: String,
    val order: String,
    val case: String,
    val event: String) {

    fun mapping():String {
        return "select * from $table where $case order by $order"
    }
}