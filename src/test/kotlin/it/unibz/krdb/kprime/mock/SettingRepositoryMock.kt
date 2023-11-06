package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.setting.SettingRepository

class SettingRepositoryMock: SettingRepository {
    override fun write(setting: Setting) {
        TODO("SettingRepositoryMock.write() Not yet implemented")
    }

    override fun read(): Setting {
        //TODO("SettingRepositoryMock.read() Not yet implemented")
        return Setting("","","","","","")
    }

}