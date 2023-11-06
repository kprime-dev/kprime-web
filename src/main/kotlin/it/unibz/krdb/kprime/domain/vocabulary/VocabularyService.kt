package it.unibz.krdb.kprime.domain.vocabulary

import it.unibz.krdb.kprime.adapter.jackson.file.VocabularyFileRepository
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.SettingService

class VocabularyService(val settingService: SettingService): VocabularyServices {

    private val vocabularyRepo : VocabularyRepositoryBuilder = VocabularyFileRepository()

    override fun readInstanceVocabularies(): List<Vocabulary> {
        return vocabularyRepo.build(settingService.getInstanceDir()).findAll()
    }

    override fun writeInstanceVocabularies(vocabularies:List<Vocabulary>) {
        vocabularyRepo.build(settingService.getInstanceDir()).saveAll(vocabularies)
    }

    override fun readContextVocabularies(prjContextLocation: PrjContextLocation): List<Vocabulary> {
        println("VOCABULARY:prjContextLocation.value[${prjContextLocation.value}]")
        return vocabularyRepo.build(prjContextLocation.value+".kprime/").findAll()
    }

    override fun writeContextVocabularies(prjContextLocation: PrjContextLocation, vocabularies:List<Vocabulary>) {
        vocabularyRepo.build(prjContextLocation.value+".kprime/").saveAll(vocabularies)
    }



}