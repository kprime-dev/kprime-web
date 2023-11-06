package it.unibz.krdb.kprime.domain.user

import it.unibz.krdb.kprime.domain.Repository

interface UserRepositoryBuilder {
    fun build(location: String): Repository<User>
}