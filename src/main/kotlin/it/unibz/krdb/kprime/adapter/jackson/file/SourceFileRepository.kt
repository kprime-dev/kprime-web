package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.source.SourceRepositoryBuilder
import java.io.File


internal class SourceFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : SourceRepositoryBuilder, JsonFileContextRepository<Source>() {

    companion object {
        const val DIR_REPO = "sources/"
        const val FILENAME_REPO = "sources.json"
    }

    override fun entitiesFromFile(): List<Source> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Source>>() {}
            )
    }

    override fun build(location: String): Repository<Source> {
        val repo = SourceFileRepository(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
}