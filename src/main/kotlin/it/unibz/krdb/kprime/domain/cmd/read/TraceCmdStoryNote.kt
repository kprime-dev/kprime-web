package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentInteger
import it.unibz.krdb.kprime.domain.trace.TraceFileName
import it.unibz.krdb.kprime.view.controller.StoryController

object TraceCmdStoryNote: TraceCmd {
    override fun getCmdName(): String {
        return "story-note"
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

    private enum class ArgNames { FILEPATH,NOTE_INDEX }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.FILEPATH.name, "Filesystem path to story file.") required true,
            TraceCmdArgumentInteger(ArgNames.NOTE_INDEX.name, "Story cell index.") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextLocation = context.env.prjContextLocation
        if (contextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val filepath =  args[ArgNames.FILEPATH.name] as String
        val noteIndex = args[ArgNames.NOTE_INDEX.name] as Int
        val contextName = context.env.prjContextName
        val traceName = TraceFileName(filepath).getTraceName()
        val traceFileName = TraceFileName(filepath)
        context.pool.storyService.readNotes(contextName, traceName, traceFileName, edit=true,).fold(
            onSuccess = { result -> return successMessage(result, noteIndex-1) },
            onFailure = { return TraceCmdResult() failure "No document '${filepath}' found." }
        )
    }

    private fun successMessage(
        result: List<StoryController.Note>,
        noteIndex: Int
    ): TraceCmdResult {
        return TraceCmdResult() message result[noteIndex].title }

}