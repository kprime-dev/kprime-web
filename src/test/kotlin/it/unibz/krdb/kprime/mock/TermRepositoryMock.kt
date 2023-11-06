package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.term.TermRepositoryBuilder

class TermRepositoryMock: TermRepositoryBuilder {

    override fun build(location: String): Repository<Term> {
        TODO("Not yet implemented")
    }
}