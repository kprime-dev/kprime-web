package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import it.unibz.krdb.kprime.domain.vocabulary.VocabularyRepositoryBuilder
import java.io.File


internal class VocabularyFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : VocabularyRepositoryBuilder, JsonFileContextRepository<Vocabulary>() {

    companion object {
        const val DIR_REPO = "vocabularies/"
        const val FILENAME_REPO = "vocabularies.json"
    }

    override fun entitiesFromFile(): List<Vocabulary> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Vocabulary>>() {}
            )
    }

    override fun build(location: String): Repository<Vocabulary> {
        val repo = VocabularyFileRepository()
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
}