package it.unibz.krdb.kprime.domain.term

@JvmInline
value class LabelField(private val value:String) {

    override fun toString(): String {
        if (!value.contains(":") && value!="_") return ":$value"
        return value
    }

    fun prefix(): String {
        return if (value.contains(":"))
            value.substringBefore(":")
        else ""
    }

    fun suffix(): String {
        return if (value.contains(":"))
            value.substringAfter(":")
        else value
    }

}