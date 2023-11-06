package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.trace.TraceFileName

object TraceCmdStory: TraceCmd {
    override fun getCmdName(): String {
        return "story"
    }

    override fun getCmdDescription(): String {
        return "Get one story content."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.STORY,
        ).joinToString()
    }

    private enum class ArgNames { FILEPATH }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.FILEPATH.name, "Filesystem path to story file.") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val filepath =  args[ArgNames.FILEPATH.name] as String
        val contextName = context.env.prjContextName
        val traceName = TraceFileName(filepath).getTraceName()
        val traceFileName = TraceFileName(filepath)
        context.pool.storyService.getDocumentMd(contextName, traceName, traceFileName).fold(
            onSuccess = { result -> return TraceCmdResult() message result },
            onFailure = { return TraceCmdResult() failure "No document '${filepath}' found." }
        )
    }

    @Deprecated("use executeMap")
    fun executeMapOld(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val traceArg = "" //args[Arg.TRACE_FILE_NAME.name]  as String? ?: ""
        println(" ------------- TraceCmdStory executeMap:\n $args")
        val traceName = traceArg.substringBeforeLast("/")
        val traceFileName = traceArg.substringAfterLast("/")
        val prjContextName = context.env.prjContextName.value
        println(" Read story `$prjContextName` traceName : `$traceName`")
        val project = context.pool.prjContextService.projectByName(prjContextName)
            ?: return TraceCmdResult() failure "no project '$prjContextName' found."
        val projectTraceFileContent = context.pool.traceService.getTraceFileContent(traceName,traceFileName,project.location)
        var result = "Ok. story `$prjContextName` `$traceName` `$traceFileName`. "+System.lineSeparator()
        result += projectTraceFileContent
        return TraceCmdResult() message result
    }
}