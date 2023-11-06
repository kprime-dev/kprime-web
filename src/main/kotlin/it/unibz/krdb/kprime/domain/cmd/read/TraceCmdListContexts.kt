package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.project.PrjContext

object TraceCmdListContexts: TraceCmd {
    override fun getCmdName(): String {
        return "contexts"
    }

    override fun getCmdDescription(): String {
        return "List all contexts."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.CONTEXT,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return emptyList()
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val projects = context.pool.prjContextService.readAllProjects()
        val currentProjectName = context.env.prjContextName.value
        return TraceCmdResult()  message successMessage(projects,currentProjectName) payload projects
    }

    private fun successMessage(projects: List<PrjContext>,currentProjectName: String): String {
        var result = "Ok. ${projects.size} projects. " + System.lineSeparator()
        result += "CURRENT: "+ currentProjectName + System.lineSeparator()
        result += "ACTIVE:"+ System.lineSeparator() + projects
            .filter { it.isActive() }
            .sortedBy { it.name }
            .joinToString(System.lineSeparator()) { " (${it.id}) ${it.name} " }
        result += System.lineSeparator() + "INACTIVE:"+ System.lineSeparator() + projects
            .filter { !it.isActive() }
            .sortedBy { it.name }
            .joinToString(System.lineSeparator()) { " (${it.id})  ${it.name} " }
        result += System.lineSeparator() + "ABSTRACT:"+ System.lineSeparator() + projects
            .filter { it.isAbstract() }
            .sortedBy { it.name }
            .joinToString(System.lineSeparator()) { " (${it.id})  ${it.name} " }
        return result
    }

}