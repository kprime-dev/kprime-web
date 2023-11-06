package it.unibz.krdb.kprime.domain.project

import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Repository

interface PrjContextRepositoryBuilder {
    fun build(location: String): Repository<PrjContext>
}