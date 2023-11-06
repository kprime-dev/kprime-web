package it.unibz.krdb.kprime.domain

@JvmInline
value class ContextName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_CONTEXT_NAME = ContextName("")
    }
}