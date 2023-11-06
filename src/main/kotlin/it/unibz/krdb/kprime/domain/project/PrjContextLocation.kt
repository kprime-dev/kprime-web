package it.unibz.krdb.kprime.domain.project

@JvmInline
value class PrjContextLocation (val value:String) {
    fun isEmpty():Boolean = value.isEmpty()

    companion object {
        val NO_PROJECT_LOCATION = PrjContextLocation("")
    }
}

