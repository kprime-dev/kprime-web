package it.unibz.krdb.kprime.domain.cmd.delete

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentQName
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.term.LabelField


object TraceCmdRemLabelInstance: TraceCmd {
    override fun getCmdName(): String {
        return "rem-label-instance"
    }

    override fun getCmdDescription(): String {
        return "Remove one label to current instance repository."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <subject> <predicate> <object>"
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
            TraceCmdArgumentQName(ArgNames.SUBJECT.name, "subject of label", 3, 20) required true,
            TraceCmdArgumentQName(ArgNames.PREDICATE.name, "predicate of label", 3, 20) required true,
            TraceCmdArgumentFreeText(ArgNames.cobjects.name, "objects of label", 3, 20, pattern = "^[a-zA-Z0-9_:]*$") required true,
        )
    }


    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val subject = args[ArgNames.SUBJECT.name] as String
        val predicate = args[ArgNames.PREDICATE.name] as String
        val cobject = args[ArgNames.cobjects.name] as String

        val iriContext = "${context.env.prjContextIRI.value}${context.env.prjContextName.value}"
        val rdfDataDir = RdfService.getInstanceRdfDataDir(context.pool.settingService.getInstanceDir())
        val successMessage = context.pool.rdfService
            .remove(iriContext, LabelField(subject), LabelField(predicate), cobject, rdfDataDir)
            .onFailure {
                return TraceCmdResult() failure "Can't remove the Label. ${it.message}"
            }.getOrElse { "Unknown result for remove Label." }
        return TraceCmdResult() message successMessage
    }
}