package it.unibz.krdb.kprime.domain.term

import org.jetbrains.kotlin.util.removeSuffixIfPresent
import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.nextGid

data class Term(
        val name:String,
        val category:String,
        val relation:String,
        val type:String,
        val url:String,
        val description:String,
        val labels:String,
        val gid : Gid = nextGid(),
        val properties : String = ""
) {

    var constraint :String? =""
    var typeExpanded :String? =""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Term
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "($name ='$description', labels='$labels')"
    }


    fun <T> List<T>.odd() = filterIndexed{ index, it -> index%2 == 0 }
    fun <T> List<T>.even() = filterIndexed{ index, it -> index%2 != 0 }

}
