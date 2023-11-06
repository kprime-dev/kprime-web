package it.unibz.krdb.kprime.domain.expert

import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Repository

interface ExpertRepositoryBuilder {
    fun build(location: String): Repository<Expert>
}