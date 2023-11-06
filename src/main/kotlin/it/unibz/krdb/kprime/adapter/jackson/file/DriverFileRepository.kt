package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.source.Driver
import it.unibz.krdb.kprime.domain.source.DriverRepositoryBuilder
import java.io.File


internal class DriverFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : DriverRepositoryBuilder, JsonFileContextRepository<Driver>() {

    companion object {
        const val DIR_REPO = "drivers/"
        const val FILENAME_REPO = "drivers.json"
    }

    override fun entitiesFromFile(): List<Driver> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Driver>>() {}
            )
    }

    override fun build(location: String): Repository<Driver> {
        val repo = DriverFileRepository(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
    
}