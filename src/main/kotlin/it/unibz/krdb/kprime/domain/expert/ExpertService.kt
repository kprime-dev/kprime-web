package it.unibz.krdb.kprime.domain.expert

import it.unibz.krdb.kprime.domain.setting.SettingService

class ExpertService(private val settingService: SettingService,
                    private val expertRepo: ExpertRepositoryBuilder) {
    fun addExpert(newExpert: Expert) {
        expertRepo.build(settingService.getInstanceDir()).save(newExpert)
    }

    fun listExperts(): List<Expert> {
        return expertRepo.build(settingService.getInstanceDir()).findAll()
    }

    fun getExpert(expertName: String): Expert? {
        return expertRepo.build(settingService.getInstanceDir()).findFirstBy { it.name == expertName }
    }

    fun replace(nameToReplace:String, expert: Expert) {
        val repo = expertRepo.build(settingService.getInstanceDir())
        repo.update(expert) { it.name == nameToReplace }
    }

    fun dropExpert(expert: Expert) {
        expertRepo.build(settingService.getInstanceDir()).delete { it.name != expert.name }
    }
}