package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.term.TermRepositoryBuilder
import java.io.File


internal class TermFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : TermRepositoryBuilder, JsonFileContextRepository<Term>() {

    companion object {
        const val DIR_REPO = "terms/"
        const val FILENAME_REPO = "terms.json"
    }

    override fun entitiesFromFile(): List<Term> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Term>>() {}
            )
    }

    override fun build(location: String): Repository<Term> {
        val repo = TermFileRepository(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
}