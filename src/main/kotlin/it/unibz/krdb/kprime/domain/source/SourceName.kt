package it.unibz.krdb.kprime.domain.source

@JvmInline
value class SourceName (val value:String?) {
    fun isEmpty():Boolean {return value?.isEmpty()?:true}

    companion object {
        const val NO_SOUCE_NAME_VALUE = ""
        val NO_SOURCE_NAME = SourceName(NO_SOUCE_NAME_VALUE)
    }

    fun getValue():String {
        return value?: NO_SOUCE_NAME_VALUE
    }

    override fun toString(): String {
        return value?: NO_SOUCE_NAME_VALUE
    }

}
