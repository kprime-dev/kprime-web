package it.unibz.krdb.kprime.domain.cmd.rdf

import it.unibz.krdb.kprime.domain.Namespace
import it.unibz.krdb.kprime.domain.Prefix
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary

object TraceCmdSparqlLabel : TraceCmd {
    override fun getCmdName(): String {
        return "sparql"
    }

    override fun getCmdDescription(): String {
        return "SPARQL over context labels."
    }

    private enum class ArgNames { SPARQL_QUERY }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFreeText(ArgNames.SPARQL_QUERY.name, "SPARQL query.") required true
        )
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.LABEL,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val sparqlQuery = args[ArgNames.SPARQL_QUERY.name] as String
        //val rdfDataDir = RdfService.getInstanceRdfDataDir(context.pool.settingService.getInstanceDir())
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val instanceVocabularies = context.pool.termService.readInstanceVocabularies()
        val contextVocabularies = context.pool.termService.readContextVocabularies(context.env.prjContextLocation)
        val sparqlQueryWithPrefixes = allContextPrefixes(sparqlQuery, contextVocabularies)
        val successMessage = context.pool.rdfService.query(sparqlQueryWithPrefixes,rdfDataDir)
            .onFailure {
                return TraceCmdResult() failure "Can't query for Labels. ${it.message}"
            }.getOrElse { "Unknown result for query Label." }
        return TraceCmdResult() message successMessage
    }

    private fun prefixes(
        sparqlQuery: String,
        instanceVocabularies: List<Vocabulary>,
        contextVocabularies: List<Vocabulary>
    ): String {
        val mapPrefixToAdd = mutableMapOf<Prefix,Namespace>()
        val tokens = sparqlQuery.split(" ").map { it.trim() }
        val mapPrefixInstance = instanceVocabularies.associate { it.prefix to it.namespace }
        tokens
            .filter { it.contains(":") }
            .map { it.substringBefore(":") }
            .filter { mapPrefixInstance[it]!=null }
            .map { mapPrefixToAdd[it]=mapPrefixInstance[it]?:Namespace() }
        val mapPrefixContext = contextVocabularies.associate { it.prefix to it.namespace }
        tokens
            .filter { it.contains(":") }
            .map { it.substringBefore(":") }
            .filter { mapPrefixContext[it]!=null }
            .map { mapPrefixToAdd[it]=mapPrefixContext[it]?:Namespace() }
        var sparqlPrefix = ""
        for (entry in mapPrefixToAdd) {
            sparqlPrefix += "PREFIX ${entry.key} <${entry.value}> "
        }
        return sparqlPrefix + sparqlQuery
    }

    private fun allPrefixes(
        sparqlQuery: String,
        instanceVocabularies: List<Vocabulary>,
        contextVocabularies: List<Vocabulary>
    ): String {
        var sparqlPrefix = ""
        for (entry in instanceVocabularies) {
            sparqlPrefix += "PREFIX ${entry.prefix} <${entry.namespace}> "
        }
        for (entry in contextVocabularies) {
            sparqlPrefix += "PREFIX ${entry.prefix} <${entry.namespace}> "
        }
        return sparqlPrefix + sparqlQuery
    }

    private fun allContextPrefixes(
        sparqlQuery: String,
        contextVocabularies: List<Vocabulary>
    ): String {
        var sparqlPrefix = ""
        for (entry in contextVocabularies) {
            sparqlPrefix += "PREFIX ${entry.prefix} <${entry.namespace}> "
        }
        return sparqlPrefix + sparqlQuery
    }

}