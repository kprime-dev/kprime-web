package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary

object TraceCmdListVocabularies: TraceCmd {
    override fun getCmdName(): String {
        return "vocabularies"
    }

    override fun getCmdDescription(): String {
        return "List current termbase vocabularies in use."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.VOCABULARY,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = context.env.prjContextLocation
        val prjContextName = context.env.prjContextName
        val instanceVocabularies = context.pool.termService.readInstanceVocabularies()
        val contextVocabularies = context.pool.termService.readContextVocabularies(prjContextLocation)
        return TraceCmdResult() message successMessage(prjContextName,instanceVocabularies,contextVocabularies)
    }

    private fun successMessage(prjContextName: PrjContextName, instanceVocabularies: List<Vocabulary>, contextVocabularies: List<Vocabulary>): String {
        val instanceList = instanceVocabularies
            .mapIndexed { index, vocabulary -> "(I${index+1}) $vocabulary" }
            .joinToString(System.lineSeparator())
        val contextList = contextVocabularies
            .mapIndexed { index, vocabulary -> "(C${index+1}) $vocabulary" }
            .joinToString(System.lineSeparator())
        return """Vocabularies (${instanceVocabularies.size+contextVocabularies.size}) ${prjContextName.value} :
            $instanceList
            $contextList
        """.trimMargin()
    }


}