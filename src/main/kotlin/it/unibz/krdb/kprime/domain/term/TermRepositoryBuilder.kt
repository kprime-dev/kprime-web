package it.unibz.krdb.kprime.domain.term

import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Repository

interface TermRepositoryBuilder {
    fun build(location: String): Repository<Term>
}