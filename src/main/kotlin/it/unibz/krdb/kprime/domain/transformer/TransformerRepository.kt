package it.unibz.krdb.kprime.domain.transformer

interface TransformerRepository {
    fun getAll(): List<TransformerDesc>

}
