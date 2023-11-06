package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFilePath
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import java.io.File

object TraceCmdAddDoc: TraceCmd {
    override fun getCmdName(): String {
        return "add-doc"
    }

    override fun getCmdDescription(): String {
        return "Adds one doc from current trace."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.STORY,
            TraceCmd.Topic.CONCEPTUAL
        ).joinToString()
    }

    internal enum class ArgNames {
        TEMPLATE_NAME,NEW_STORY_NAME;
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFilePath(ArgNames.TEMPLATE_NAME.name," Template name e.g. readme.txt") required true,
            TraceCmdArgumentFilePath(ArgNames.NEW_STORY_NAME.name," New story file name e.g. myreadme ") required true,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val templateName = args[ArgNames.TEMPLATE_NAME.name] as String
        val newDocName = args[ArgNames.NEW_STORY_NAME.name] as String
        val docTemplateDir = context.pool.settingService.getTemplatesDir()
        val docTemplateFileName = docTemplateDir + templateName
        val fileDocTemplate = File(docTemplateFileName)
        val fileDocTemplateExtension = fileDocTemplate.extension
        if (!fileDocTemplate.exists()) return TraceCmdResult() failure "Template doesn't exists."
        val newDocFileName = context.pool.settingService.getWorkingDir()+"/$newDocName.$fileDocTemplateExtension"
        fileDocTemplate.copyTo(File(newDocFileName))
        return TraceCmdResult() message "Trace $newDocName created."
    }

}
