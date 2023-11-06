package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentTextPattern
import it.unibz.krdb.kprime.domain.trace.TraceName

object TraceCmdFromTemplate: TraceCmd {
    override fun getCmdName(): String {
        return "from-template"
    }

    override fun getCmdDescription(): String {
        return "creates a new story file with 'story-name' in current context from tamplate with 'tamplate-name'"
    }

    override fun getCmdTopics(): String {
        return listOf(TraceCmd.Topic.WRITE,TraceCmd.Topic.STORY).joinToString()
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return argConstr.values.toList()
    }

    private enum class argNames { TEMPLATE_NAME,STORY_NAME, TRACE_NAME }
    private val argConstr = mapOf<argNames, TraceCmdArgumentI>(
        argNames.TEMPLATE_NAME to
                (TraceCmdArgumentFilePath(
                argNames.TEMPLATE_NAME.name,
                "name of the template",
                3,50) required true) ,
        argNames.STORY_NAME to
                (TraceCmdArgumentText(
                    argNames.STORY_NAME.name,
                    "name of the story",
                    3, 50) required true),
        argNames.TRACE_NAME to
                (TraceCmdArgumentFilePath(
                    argNames.TRACE_NAME.name,
                    "name of the story",
                    3, 50) required true)
            )

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        println("TraceCmdFromTemplate.executeMap BEGIN")
        val contextName =  context.env.prjContextName
        val templateName =  args[argNames.TEMPLATE_NAME.name] as String
        val storyName =  args[argNames.STORY_NAME.name] as String
        val traceName = args[argNames.TRACE_NAME.name] as String

        context.pool.storyService.addStory(contextName, TraceName(traceName) , storyName, templateName)
            .onSuccess { return TraceCmdResult() message "OK! created $it story:'$storyName' from template:'$templateName' ."}
            .onFailure { it.printStackTrace(); return TraceCmdResult() failure "Can't create a new story. ${it.message}" }

        println("TraceCmdFromTemplate.executeMap END")
        return TraceCmdResult() message getCmdUsage()
    }

}