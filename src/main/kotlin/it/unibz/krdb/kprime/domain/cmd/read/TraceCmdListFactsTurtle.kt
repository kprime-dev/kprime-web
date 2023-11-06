
package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.adapter.fact.FactTurtleDescriptor
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult

object TraceCmdListFactsTurtle : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "facts-turtle"
    }

    override fun getCmdDescription(): String {
        return "List all facts from current database, or about a specific table-name in Turtle format."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " [<table-name>]"
    }

    override fun getCmdTopics(): String {
        return "read,conceptual,fact"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        var filter = ""
        if (tokens.size==2) filter = tokens[1]
        val database = context.env.database
        //val projectLocation = context.projectService.
        val trace = context.env.currentTrace?: return TraceCmdResult() failure "no trace."
        val traceFileName = context.env.currentTraceFileName?:return TraceCmdResult() failure "no trace file name."
        val terms = context.pool.termService.getAllTerms(trace,traceFileName, PrjContext.NO_PrjContext.location)
        var facts = FactTurtleDescriptor().describe(database,terms)
        return TraceCmdResult() message facts
    }

}


