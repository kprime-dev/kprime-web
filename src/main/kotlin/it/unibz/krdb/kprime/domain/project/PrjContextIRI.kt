package it.unibz.krdb.kprime.domain.project

@JvmInline
value class PrjContextIRI (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_IRI_NAME = PrjContextIRI("")
    }
}