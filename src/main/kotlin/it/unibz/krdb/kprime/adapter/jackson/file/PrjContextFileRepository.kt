package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextRepositoryBuilder
import java.io.File


internal class PrjContextFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : PrjContextRepositoryBuilder, JsonFileContextRepository<PrjContext>() {

    companion object {
        const val DIR_REPO = "projects/"
        const val FILENAME_REPO = "projects.json"
    }

    override fun entitiesFromFile(): List<PrjContext> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<PrjContext>>() {}
            )
    }

    override fun build(location: String): Repository<PrjContext> {
        val repo = PrjContextFileRepository(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
    
}