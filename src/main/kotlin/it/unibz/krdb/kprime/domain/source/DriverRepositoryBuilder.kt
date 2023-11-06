package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.ContextRepository
import it.unibz.krdb.kprime.domain.Repository

interface DriverRepositoryBuilder {
    fun build(location: String): Repository<Driver>
}