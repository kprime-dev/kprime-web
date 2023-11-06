package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.expert.Expert
import it.unibz.krdb.kprime.domain.expert.ExpertRepositoryBuilder
import java.io.File


internal class ExpertFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : ExpertRepositoryBuilder, JsonFileContextRepository<Expert>() {

    companion object {
        const val DIR_REPO = "experts/"
        const val FILENAME_REPO = "experts.json"
    }

    override fun entitiesFromFile(): List<Expert> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Expert>>() {}
            )
    }

    override fun build(location: String): Repository<Expert> {
        val repo = ExpertFileRepository(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
    
}