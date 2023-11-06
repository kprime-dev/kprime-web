package it.unibz.krdb.kprime.domain

@JvmInline
value class TemplateName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_TEMPLATE_NAME = TemplateName("")
    }
}