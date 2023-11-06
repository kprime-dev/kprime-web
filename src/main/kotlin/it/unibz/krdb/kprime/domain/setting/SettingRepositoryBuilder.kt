package it.unibz.krdb.kprime.domain.setting

import it.unibz.krdb.kprime.domain.Repository

interface SettingRepositoryBuilder {
    fun build(location: String): Repository<Setting>
}