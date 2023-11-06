package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.term.Term

object TraceCmdSetTerm : TraceCmd {
    override fun getCmdName(): String {
        return "set-term"
    }

    override fun getCmdDescription(): String {
        return "Set attributes of one term ."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.TERM,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { TERM_NAME, description, category, relation, type, url, labels }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.TERM_NAME.name, "Term name.") required true,
            TraceCmdArgumentFreeText(ArgNames.description.name, "Term description.") required false,
            TraceCmdArgumentText(ArgNames.category.name, "Term Category.") required false,
            TraceCmdArgumentText(ArgNames.relation.name, "Term relation.") required false,
            TraceCmdArgumentText(ArgNames.type.name, "Term type.") required false,
            TraceCmdArgumentText(ArgNames.url.name, "Term URL.") required false,
            TraceCmdArgumentText(ArgNames.labels.name, "Term labels.") required false,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val name =  args[ArgNames.TERM_NAME.name] as String
        val description =  args[ArgNames.description.name] as String?
        val category =  args[ArgNames.category.name] as String?
        val relation =  args[ArgNames.relation.name] as String?
        val type =  args[ArgNames.type.name] as String?
        val url =  args[ArgNames.url.name] as String?
        val labels =  args[ArgNames.labels.name] as String?

        val prjContextLocation = context.env.prjContextLocation
        if (prjContextLocation.isEmpty()) TraceCmdResult() failure  "Context is required to remove a goal."
        val traceName = context.env.currentTrace
        val traceFileName = context.env.currentTraceFileName ?: return TraceCmdResult() failure "No current trace file."

        var changed = ""
        context.pool.termService.getTermByName(traceName, traceFileName, prjContextLocation.value, name)
            .onFailure {  return TraceCmdResult() failure  "Term $name to update not found." }
            .onSuccess {    termByName -> Term(
                description = if (description!=null) { changed+="description "; description  } else termByName.description,
                category = if (category!=null) { category; changed+="category "; category } else termByName.category,
                relation = if (relation!=null) { changed+="relation "; relation;  } else termByName.relation,
                type = if (type!=null) { changed+="type "  ; type } else termByName.type,
                url = if (url!=null) {  changed+="url "; url } else termByName.url,
                labels = if (labels!=null) { changed+="labels " ;  labels; } else termByName.labels,
                name = name
            )
                if (changed.isNotEmpty())
                    context.pool.termService.updateTerm(prjContextLocation,termByName)
            }
        return if (changed.isNotEmpty()) TraceCmdResult() message "Term $name $changed setted."
        else TraceCmdResult() warning "Nothing Changed"
    }

}