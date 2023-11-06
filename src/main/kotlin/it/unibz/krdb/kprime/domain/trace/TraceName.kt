package it.unibz.krdb.kprime.domain.trace

@JvmInline
value class TraceName (val value:String) {
    fun isEmpty():Boolean {return value.isEmpty()}

    companion object {
        val NO_TRACE_NAME = TraceName("")
    }

    override fun toString(): String {
        return value
    }

    fun toDirName():String {
        var dirName = value.replace("___", "/")
        if (!dirName.endsWith("/")) dirName += "/"
        return dirName
    }

    fun toUrlName():String {
        if (value.isBlank()) return "___"
        return value.replace("/","___")
    }
}
