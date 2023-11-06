package it.unibz.krdb.kprime.domain.source

interface SourceRepository {

    fun allSources(): List<Source>
    fun putSources(sources: List<Source>)
    fun allContextSources(contextDir: String): List<Source>
}
