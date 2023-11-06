package it.unibz.krdb.kprime.domain.setting

interface SettingRepository {
    fun write(setting: Setting)
    fun read(): Setting
}