package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.adapter.RdfStatement
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.term.LabelField
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import unibz.cs.semint.kprime.adapter.repository.ResultList
import unibz.cs.semint.kprime.domain.db.Database

interface RdfService {

    companion object {
        private const val RDF_DIR = "rdf4j/"

        fun getPrjContextRdfDataDir(prjContextLocation: PrjContextLocation):String {
            return prjContextLocation.value + ".kprime/"+ RDF_DIR
        }

        fun getInstanceRdfDataDir(instanceDir: String):String {
            return instanceDir + RDF_DIR
        }

    }

    fun find(iriContext: String, subject: LabelField, predicate: LabelField, cobject: String, rdfDataDir: String): Result<String>
    fun list(iriContext: String, rdfDataDir: String): Result<String>
    fun toTurtle(
        projectName: String,
        vocabularies: List<Vocabulary>,
        terms: List<Term>,
        iriContext: String,
        prjContextLocation: PrjContextLocation
    ): String
    fun findStatements(iriContext: String, subject: LabelField, predicate: LabelField, cobject: String, rdfDataDir: String): Result<List<RdfStatement>>
    fun listStatements(): Result<List<RdfStatement>>
    fun import(
        iriContext: String,
        projectLocation: PrjContextLocation,
        inputFilePath: String,
        rdfDataDir: String
    ): Result<String>
    fun removeNamespace(iriContext: String, rdfDataDir: String): Result<String>
    fun query(sparqlQuery: String, rdfDataDir: String): Result<String>
    fun queryList(sparqlQuery: String, rdfDataDir: String): Result<ResultList>
    fun toDatabaseFromOntouml(iriContext: String, rdfDataDir: String, addLabels:Boolean): Database
    fun add(
        iriContext: String,
        subject: LabelField,
        predicate: LabelField,
        cobject: String,
        rdfDataDir: String
    ): Result<String>
    fun remove(
        iriContext: String,
        subject: LabelField,
        predicate: LabelField,
        cobject: String,
        rdfDataDir: String
    ): Result<String>
}