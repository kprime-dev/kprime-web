package it.unibz.krdb.kprime.domain.trace

@JvmInline
value class TraceFileName(private val value:String) {

    fun isEmpty():Boolean {return value.isEmpty()}

    override fun toString(): String {
        return value
    }

    fun getTraceName():TraceName {
        return TraceName(value.substringBeforeLast("/").replace("//","/"))
    }

    fun getFileName():String {
        return value.substringAfterLast("/")
    }
}