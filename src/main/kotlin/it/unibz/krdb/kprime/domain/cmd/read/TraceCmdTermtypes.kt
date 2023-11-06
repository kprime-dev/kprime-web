package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdTermtypes: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "termtypes"
    }

    override fun getCmdTopics(): String {
        return "read,logic,term"
    }

    override fun getCmdDescription(): String {
        return "Shows the list of vocabulary term types available."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <vocabulary> [<termtype>]"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()

        val tokens = command.split(" ")
        val termName: String
        val vocabularyName: String
        val result: String
        when (tokens.size) {
            1 -> result = getCmdUsage()
            2 -> {
                vocabularyName = tokens[1]
                result = vocabulary(context,vocabularyName)
            }
            3 -> {
                vocabularyName = tokens[1]
                termName = tokens[2]
                result = term(context,vocabularyName,termName)
            }
            else -> result = getCmdUsage()
        }
        return TraceCmdResult() message result

    }

    private fun term(context: CmdContext, vocabularyName: String, termName: String): String {
        var listOfTermtypes = "No termtypes available."
        if (vocabularyName=="schema") {
            listOfTermtypes = context.pool.termService.schemaTerms()
                    .filter { it.name.endsWith("/$termName") }
                    .sortedBy { it.name }
                    .joinToString(System.lineSeparator()) { "%-10s\t%-30s\t%-30s".format(it.name, it.relation, it.type) }
        }
        return listOfTermtypes
    }

    private fun vocabulary(context: CmdContext, vocabularyName: String): String {
        var listOfTermtypes = "No termtypes available."
        if (vocabularyName=="schema") {
            listOfTermtypes = context.pool.termService.schemaTerms()
                    .filter { it.relation.endsWith("#comment") }
                    .sortedBy { it.name }
                    .joinToString(System.lineSeparator()) { "%-10s\t%-30s".format(it.name, it.type) }
        }
        return listOfTermtypes
    }

}