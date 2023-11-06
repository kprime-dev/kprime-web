package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.AggregateID
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserRepositoryBuilder
import java.io.File


internal class UserFileRepository(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : UserRepositoryBuilder, JsonFileContextRepository<User>() {

    companion object {
        const val DIR_REPO = "users/"
        const val FILENAME_REPO = "users.json"
    }

    override fun entitiesFromFile(): List<User> {
        val readValue = jacksonObjectMapper()
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object: TypeReference<List<User>>() {}
            )
        return readValue
    }

    override fun build(location: String): Repository<User> {
        val repo = UserFileRepository()
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }

    override fun findAll(): List<User> {
        val findAll = super.findAll()
        if (findAll.isEmpty())
            return listOf(User(AggregateID.build().key,"admin",User.ROLE.ADMIN.name,"","pass",""))
        return findAll
    }
}