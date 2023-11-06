package it.unibz.krdb.kprime.adapter.jackson.file

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.setting.SettingRepository
import java.io.File

class SettingFileRepository(var repoDir:String) : SettingRepository {

    override fun write(setting: Setting) {
        println("SettingFileRepository.write setting for project name:${setting.projectName}")
        val settingJson = jacksonObjectMapper().writeValueAsString(setting)
        if (repoDir.isEmpty()) repoDir = System.getenv("KPRIME_HOME")?:""
        if (repoDir.isEmpty()) return
        val settingsDir = repoDir+"settings/"
        if (!File(settingsDir).exists()) File(settingsDir).mkdir()
        File(settingsDir +"settings.json").writeText(settingJson, Charsets.UTF_8)
    }

    override fun read(): Setting {
        if (repoDir.isEmpty()) repoDir = System.getenv("KPRIME_HOME") ?: ""
        if (repoDir.isEmpty()) repoDir = "./"
        val settingsFile = File(repoDir + "settings/settings.json")
        if (!settingsFile.exists()) {
            write(Setting(repoDir, "", "", "", "", ""))
        }
        val jsonContent = settingsFile.readText(Charsets.UTF_8)
        return jacksonObjectMapper().readValue(jsonContent)
    }

}