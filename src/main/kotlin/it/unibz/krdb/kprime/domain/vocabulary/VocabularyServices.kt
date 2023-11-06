package it.unibz.krdb.kprime.domain.vocabulary

import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.trace.TraceName

interface VocabularyServices {
    fun readInstanceVocabularies(): List<Vocabulary>
    fun writeInstanceVocabularies(vocabularies:List<Vocabulary>)
    fun readContextVocabularies(prjContextLocation: PrjContextLocation): List<Vocabulary>
    fun writeContextVocabularies(prjContextLocation: PrjContextLocation, vocabularies:List<Vocabulary>)

}