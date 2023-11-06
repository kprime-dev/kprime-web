package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.todo.TodoRepositoryBuilder
import java.io.File

internal class TodoFileRepositoryBuilder : TodoRepositoryBuilder {

    private var _repoDir = "goals/"
    private var _repoFileName = "todo.json"

    fun setRepoDir(repoDir: String) = apply {
        this._repoDir = repoDir
    }

    fun setRepoFileName(repoFileName: String) = apply {
        this._repoFileName = repoFileName
    }

    override fun build(location: String): Repository<Todo> {
        val repo = TodoFileRepository()
        repo.repoDir = location + _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }

    private class TodoFileRepository : JsonFileContextRepository<Todo>() {

        override fun entitiesFromFile(): List<Todo> {
            return jacksonObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(
                    File(repoDir + repoFileName).readText(Charsets.UTF_8),
                    object : TypeReference<List<Todo>>() {}
                )
        }

    }
}