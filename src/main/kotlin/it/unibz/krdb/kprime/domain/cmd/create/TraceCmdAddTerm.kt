package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdAddTerm : TraceCmd {
    override fun getCmdName(): String {
        return "add-term"
    }

    override fun getCmdDescription(): String {
        return "Add one term to current trace."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.TERM,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { TERM_NAME, TERM_DESCRIPTION, category, relation, type, url, labels }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TERM_NAME.name, "Term name.") required true,
            TraceCmdArgumentFreeText(ArgNames.TERM_DESCRIPTION.name, "Term description.") required true,
            TraceCmdArgumentText(ArgNames.category.name, "Term Category.") required false,
            TraceCmdArgumentText(ArgNames.relation.name, "Term relation.") required false,
            TraceCmdArgumentText(ArgNames.type.name, "Term type.") required false,
            TraceCmdArgumentText(ArgNames.url.name, "Term URL.") required false,
            TraceCmdArgumentText(ArgNames.labels.name, "Term labels.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val name =  args[ArgNames.TERM_NAME.name] as String
        val description =  args[ArgNames.TERM_DESCRIPTION.name] as String
        val category =  args[ArgNames.category.name] as String?
        val relation =  args[ArgNames.relation.name] as String?
        val type =  args[ArgNames.type.name] as String?
        val url =  args[ArgNames.url.name] as String?
        val labels =  args[ArgNames.labels.name] as String?

        val term = Term(
            name = name,
            category = category ?:"",
            relation = relation ?:"",
            type = type ?:"",
            url = url ?:"",
            description = description,
            labels = labels ?:"")

        println(term)

        val prjContextLocation = context.env.prjContextLocation

        return context.pool.termService.addTerm(prjContextLocation.value, term).fold(
            onSuccess = { TraceCmdResult() message "Added term with ${it}" payload term },
            onFailure = { TraceCmdResult() failure "Goal not added [${it.message}]." }
        )
    }

}