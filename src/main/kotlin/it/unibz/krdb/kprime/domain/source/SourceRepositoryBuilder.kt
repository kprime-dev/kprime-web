package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.Repository

interface SourceRepositoryBuilder {
    fun build(location: String): Repository<Source>
}