package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.domain.actor.ActorRepositoryBuilder
import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Page

class ActorRepositoryMock() : ActorRepositoryBuilder, ContextRepository<Actor> {
    private var actorList = mutableListOf<Actor>()

    override fun build(location: String): ContextRepository<Actor> {
        return this
    }


    override fun findFirstBy(criteria: (Actor) -> Boolean): Actor? {
        return actorList.filter (criteria).firstOrNull()
    }

    override fun save(entity: Actor) {
        return
    }

    override fun saveAll(entities: List<Actor>) {
        actorList.clear()
        actorList.addAll(entities)
    }

    override fun findByCriteria(criteria: (Actor) -> Boolean): List<Actor> {
        return actorList.filter (criteria)
    }

    override var repoDir: String
        get() = ""
        set(value) {}
    override var repoFileName: String
        get() = ""
        set(value) {}

    override fun findPage(page: Page): List<Actor> {
        return actorList.subList(page.pos,page.pos+page.size)
    }

    override fun findAll(): List<Actor> {
        return actorList
    }

    override fun delete(criteria: (Actor) -> Boolean): Int {
        val firstSize = actorList.size
        actorList.removeIf(criteria)
        return firstSize - actorList.size
    }

    override fun delete(entity: Actor): Boolean {
        actorList.remove(entity)
        return true
    }

    override fun deleteAll() {
        actorList.clear()
    }

    override fun countAll(): Int {
        return actorList.size
    }

    override fun countPages(page: Page): Int {
        return actorList.size % page.size
    }

    override fun update(entity: Actor, criteria: (Actor) -> Boolean) {
        actorList.removeIf(criteria)
        actorList.add(entity)
    }

}