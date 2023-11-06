package it.unibz.krdb.kprime.domain.term

import it.unibz.krdb.kprime.domain.project.PrjContext
import unibz.cs.semint.kprime.domain.db.Database

interface JsonApiService {
    fun openApi(project: PrjContext, database: Database, allTerms: List<Term>):String
}