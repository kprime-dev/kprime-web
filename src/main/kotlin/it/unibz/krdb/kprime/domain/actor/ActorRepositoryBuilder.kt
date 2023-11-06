package it.unibz.krdb.kprime.domain.actor

import it.unibz.krdb.kprime.domain.Repository

interface ActorRepositoryBuilder {
    fun build(location: String): Repository<Actor>
}