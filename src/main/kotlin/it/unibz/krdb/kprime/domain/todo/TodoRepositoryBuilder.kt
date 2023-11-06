package it.unibz.krdb.kprime.domain.todo

import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Repository

interface TodoRepositoryBuilder {
    fun build(location: String): Repository<Todo>
}