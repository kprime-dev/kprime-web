package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JsonFileContextRepository
import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.setting.SettingRepositoryBuilder
import java.io.File


internal class SettingFileRepository2(
    private val _repoDir: String = DIR_REPO,
    private val _repoFileName: String = FILENAME_REPO)
    : SettingRepositoryBuilder, JsonFileContextRepository<Setting>() {

    companion object {
        const val DIR_REPO = "settings/"
        const val FILENAME_REPO = "settings.json"
    }

    override fun entitiesFromFile(): List<Setting> {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object : TypeReference<List<Setting>>() {}
            )
    }

    override fun build(location: String): Repository<Setting> {
        val repo = SettingFileRepository2(DIR_REPO, FILENAME_REPO)
        repo.repoDir = location+ _repoDir
        repo.repoFileName = _repoFileName
        return repo
    }
}