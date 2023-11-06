package it.unibz.krdb.kprime.domain.vocabulary

import it.unibz.krdb.kprime.domain.Repository

interface VocabularyRepositoryBuilder {
    fun build(location: String): Repository<Vocabulary>
}