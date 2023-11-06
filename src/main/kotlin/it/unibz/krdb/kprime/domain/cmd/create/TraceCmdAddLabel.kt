package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentQName
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.term.LabelField


object TraceCmdAddLabel : TraceCmd {
    override fun getCmdName(): String {
        return "add-label"
    }

    override fun getCmdDescription(): String {
        return "Add one label to current repository."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.LABEL
        ).joinToString()
    }

    private enum class ArgNames { SUBJECT, PREDICATE, cobjects }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentQName(ArgNames.SUBJECT.name, "subject of label", 3, 50) required true,
            TraceCmdArgumentQName(ArgNames.PREDICATE.name, "predicate of label", 3, 50) required true,
            TraceCmdArgumentFreeText(ArgNames.cobjects.name, "objects of label", 3, 50,  pattern = "^[a-zA-Z0-9_:]*$") required true,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val subject = args[ArgNames.SUBJECT.name] as String
        val predicate = args[ArgNames.PREDICATE.name] as String
        val cObject = args[ArgNames.cobjects.name] as String
        val iriContext = "${context.env.prjContextIRI.value}${context.env.prjContextName.value}" //FIXME
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(context.env.prjContextLocation)
        val successMessage = context.pool.rdfService
            .add(iriContext, LabelField(subject), LabelField(predicate), cObject, rdfDataDir)
            .onFailure {
                return TraceCmdResult() failure "Can't add a new Label. ${it.message}"
            }.getOrElse { "Unknown result for add new Label." }
        return TraceCmdResult() message successMessage
    }
}