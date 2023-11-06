package it.unibz.krdb.kprime.domain.trace

// FIXME not really used.
data class Trace(
        val name:String,
        val commands:List<String>,
        val log:List<String>)