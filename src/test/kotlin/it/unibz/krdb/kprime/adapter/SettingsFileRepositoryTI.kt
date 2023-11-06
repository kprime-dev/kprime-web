package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.adapter.jackson.file.SettingFileRepository
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Read and write files on filesystem.
 */
class SettingsFileRepositoryTI {

    @Test
    fun test_settings_file_repository_write() {
        // given
        val setting = Setting("/home/nipe/Temp/kprime/","","","","","")
        val repo = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        // when
        repo.write(setting)
        // write on filesystem
    }

    @Test
    fun test_settings_file_repository_read() {
        // given
        val repo = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        // when
        val setting = repo.read()
        // then
        assertEquals("/home/nipe/Temp/kprime/",setting.workingDir)
    }

}