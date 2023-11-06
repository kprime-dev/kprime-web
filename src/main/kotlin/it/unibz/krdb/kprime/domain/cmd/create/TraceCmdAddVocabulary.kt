package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.project.PrjContextLocation

object TraceCmdAddVocabulary : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-vocabulary"
    }

    override fun getCmdDescription(): String {
        return "Adds one vocabulary"
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <prefix> <namespace> <url> <description>"
    }

    override fun getCmdTopics(): String {
        return "write,conceptual,vocabulary"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val currentTrace = context.env.currentTrace ?: return TraceCmdResult() failure  "No current trace."

        val tokens = command.split(" ")
        val prefix = tokens[1]
        val namespace = tokens[2]
        val url = tokens[3]
        val descriptionPos = command.indexOf(url)+url.length
        val description = command.substring(descriptionPos)

        val allVocabularies = context.pool.termService.readContextVocabularies(PrjContextLocation(namespace)).toMutableList()
        val vocabulary = Vocabulary(prefix,namespace,description,url)
        allVocabularies.add(vocabulary)
        context.pool.termService.writeContextVocabularies(PrjContextLocation(namespace),allVocabularies)
        return TraceCmdResult() message "Ok. vocabulary added."
    }
}