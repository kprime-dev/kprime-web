package it.unibz.krdb.kprime.adapter.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Page
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.support.append
import it.unibz.krdb.kprime.support.replace
import java.io.File

// repoDir = workingDir + DIR_USERS
abstract class JsonFileContextRepository<T> : Repository<T> {
    var repoDir:String = ""
    var repoFileName:String = ""

    override fun save(entity: T) {
        append(entity)
    }

    override fun saveAll(entities: List<T>) {
        if (!File(repoDir).isDirectory) File(repoDir).mkdir()
        entitiesToFile(entities)
    }

    override fun findByCriteria(criteria: (T) -> Boolean): List<T> {
        return  findAll().filter { u -> criteria(u) }
    }

    override fun findFirstBy(criteria: (T) -> Boolean): T? {
        return findAll().firstOrNull { u -> criteria(u) }
    }

    override fun findPage(page: Page): List<T> {
        val entities = findAll()
        val fromPos = page.size * page.pos
        if (fromPos > entities.size) return emptyList()
        var toPos = page.size * (page.pos+1)
        if (toPos > entities.size) toPos  = entities.size
        return entities.subList(fromPos, toPos)
    }

    override fun findAll(): List<T> {
        if (repoDir.isEmpty()) return emptyList()
        if (!File(repoDir+ repoFileName).exists()) return emptyList()
        return entitiesFromFile()
    }

    override fun delete(entity: T): Boolean {
        val findAll = findAll()
        val entities = findAll.filter { it != entity }
        saveAll(entities)
        return findAll.size != entities.size
    }

    override fun delete(criteria: (T) ->Boolean ): Int {
        val findAll = findAll()
        val entities = findAll.filterNot (criteria)
        saveAll(entities)
        return findAll.size - entities.size
    }

    override fun deleteAll() {
        saveAll(emptyList())
    }

    override fun countAll(): Int {
        return findAll().size
    }

    override fun countPages(page: Page): Int {
        return findAll().size / page.size
    }

    abstract fun entitiesFromFile(): List<T>

    private fun entitiesToFile(entities: List<T>) {
        if (!File(repoDir).exists()) File(repoDir).mkdirs()
        println("write entities on [$repoDir] file [$repoFileName]")
        File(repoDir + repoFileName)
                .writeText(jacksonObjectMapper().writeValueAsString(entities), Charsets.UTF_8)
    }

    override fun update(entity: T, criteria: (T) ->Boolean) {
        saveAll(findAll().replace(entity,criteria) )
    }

    private fun append(entity: T) {
        saveAll(findAll().append(entity))
    }

}