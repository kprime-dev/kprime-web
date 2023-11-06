package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.domain.actor.ActorRepositoryBuilder
import it.unibz.krdb.kprime.domain.Repository
import java.io.File


internal class ActorFileRepository(
    private val _repoDir: String = DIR_ACTORS,
    private val _repoFileName: String = FILENAME_ACTORS)
    : ActorRepositoryBuilder, JsonFileContextRepository<Actor>() {

    companion object {
        const val DIR_ACTORS = "actors/"
        const val FILENAME_ACTORS = "actors.json"
    }

    override fun entitiesFromFile(): List<Actor> {
        val readValue = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object: TypeReference<List<Actor>>() {}
            )
        return readValue
    }

    override fun build(location: String): Repository<Actor> {
        val repo = ActorFileRepository(DIR_ACTORS, FILENAME_ACTORS)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }

    override fun findAll(): List<Actor> {
        return super.findAll()
    }
}