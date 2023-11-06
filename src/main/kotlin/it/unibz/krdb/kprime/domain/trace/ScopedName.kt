package it.unibz.krdb.kprime.domain.trace

@JvmInline
value class ScopedName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    override fun toString(): String {
        return value
    }

    fun getScope():Char {
        return value[0].toUpperCase()
    }

    fun getName():String {
        return value.drop(1)
    }

    fun isContextScope():Boolean {
        return getScope()=='C'
    }

    fun isInstanceScope():Boolean {
        return getScope()=='I'
    }

}
