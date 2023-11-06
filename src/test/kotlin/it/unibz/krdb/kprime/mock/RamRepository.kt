package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.Page
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.project.PrjContext
import java.io.File

open class RamRepository<Any>: Repository<Any> {

    val list = ArrayList<Any>()

    override fun save(entity: Any) {
        list.add(entity)
    }

    override fun saveAll(entities: List<Any>) {
        list.addAll(entities)
    }

    override fun findFirstBy(criteria: (Any) -> Boolean): Any? {
        TODO("Not yet implemented")
    }

    override fun findByCriteria(criteria: (Any) -> Boolean): List<Any> {
        TODO("Not yet implemented")
    }

    override fun findPage(page: Page): List<Any> {
        val entities = findAll()
        val fromPos = page.size * page.pos
        if (fromPos > entities.size) return emptyList()
        var toPos = page.size * (page.pos+1)
        if (toPos > entities.size) toPos  = entities.size
        return entities.subList(fromPos, toPos)
    }

    override fun findAll(): List<Any> {
        return list
    }

    override fun deleteAll() {
        list.clear()
    }

    override fun countAll(): Int {
        return list.count()
    }

    override fun countPages(page: Page): Int {
        return findAll().size / page.size
    }

    override fun update(entity: Any, criteria: (Any) -> Boolean) {
        TODO("Not yet implemented")
    }

    override fun delete(criteria: (Any) -> Boolean): Int {
        val toDelete = list.filter (criteria)
        list.removeAll(toDelete.toSet())
        return toDelete.size
    }

    override fun delete(entity: Any): Boolean {
        return list.remove(entity)
    }

}