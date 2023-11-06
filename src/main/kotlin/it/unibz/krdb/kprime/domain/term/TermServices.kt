package it.unibz.krdb.kprime.domain.term

import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.trace.TraceName

interface TermServices {

    fun getAllTerms(traceName: TraceName, traceFileName: String, projectLocation: String): List<Term>
    fun addTerm(projectLocation: String, term: Term): Result<String>
    fun remTerm(projectLocation: String, termName: String):Result<String>
    fun updateTerm(projectLocation: PrjContextLocation, newTerm: Term): Result<String>
    fun writeAllTerms(projectLocation: String, traceName: TraceName, traceFileName:String, newterms: List<Term>)
    fun getTermByGid(traceName: TraceName, traceFileName: String, projectLocation: String,gid: String): Term?
    fun getTermByName(traceName: TraceName, traceFileName: String, projectLocation: String,name: String): Result<Term>
    fun listAllTerms(projectLocation: String, traceName: TraceName, traceFileName: String): List<Term>
    fun listExpandedEntities(projectLocation: String, traceName: TraceName, traceFileName: String): List<Term>
}