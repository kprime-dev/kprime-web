package it.unibz.krdb.kprime.domain

import java.util.*

data class Page (
        val size: Int,
        val pos: Int
)

data class AggregateID(val key: String) {
    companion object {
        fun build(): AggregateID {
            return AggregateID(UUID.randomUUID().toString())
        }
    }
}

interface Repository<T> {
    fun save(entity: T)
    fun saveAll(entities: List<T>)
    fun findFirstBy(criteria: (T) -> Boolean): T?
    fun findByCriteria(criteria: (T) -> Boolean): List<T>
    fun findPage(page: Page): List<T>
    fun findAll(): List<T>
    fun delete(entity: T): Boolean
    fun delete(criteria: (T) -> Boolean): Int
    fun deleteAll()
    fun countAll(): Int
    fun countPages(page: Page): Int
    fun update(entity: T, criteria: (T) -> Boolean)
}