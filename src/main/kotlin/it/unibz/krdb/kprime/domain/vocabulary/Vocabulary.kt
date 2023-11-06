package it.unibz.krdb.kprime.domain.vocabulary

import it.unibz.krdb.kprime.domain.Description
import it.unibz.krdb.kprime.domain.Namespace
import it.unibz.krdb.kprime.domain.Prefix
import it.unibz.krdb.kprime.domain.Reference

data class Vocabulary(
    val prefix: Prefix,
    val namespace: Namespace,
    val description: Description = "",
    val reference: Reference = "") {

    companion object {
        fun checkUnknownPrefixes(tokens: List<String>, knownPrefixes: List<Prefix>) : List<String> {
            return tokens.filter { it.contains(":") }
                    .map { it.substringBefore(":").trimStart()+":" }
                    .filter { !knownPrefixes.contains(it) }
        }
    }

    override fun toString(): String {
        return "'$prefix', '$namespace', description='$description', reference='$reference'"
    }


}