package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.term.LabelField
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.util.ModelBuilder
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.nativerdf.NativeStore
import java.io.File
import java.util.*

object TraceCmdFindLabel : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "find-label"
    }

    override fun getCmdDescription(): String {
        return "List database elements having the searched label."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <label>"
    }

    override fun getCmdTopics(): String {
        return "read,logical,label"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        // first token is ">"
        val subject = LabelField(tokens[1])
        val predicate = LabelField(tokens[2])
        val cobject = tokens.drop(3).joinToString(" ")

        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val successMessage = context.pool.rdfService.find(iriContext = "",
            subject, predicate, cobject,
            rdfDataDir = rdfDataDir)
            .onFailure {
                return TraceCmdResult() failure "Can't find Labels. ${it.message}"
            }.getOrElse { "Unknown result for find Label." }
        return TraceCmdResult() message successMessage
    }

}