package it.unibz.krdb.kprime.domain.project

@JvmInline
value class PrjContextName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_PROJECT_NAME = PrjContextName("")
    }
}