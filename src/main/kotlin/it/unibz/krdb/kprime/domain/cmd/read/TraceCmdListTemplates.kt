package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdListTemplates: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "templates"
    }

    override fun getCmdDescription(): String {
        return "List available markdown templates."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,document"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        val docTemplateDir = context.pool.settingService.getTemplatesDir()
        val templateList = File(docTemplateDir).list()
                                                            .filter { it.endsWith(".md")
                                                                    || it.endsWith(".chart")
                                                                    || it.endsWith(".adoc") }
                                                            .map { it.substring(0,it.length) }
                                                            .sorted()
        val resultMessage = templateList.joinToString(System.lineSeparator())
        return TraceCmdResult() message resultMessage payload templateList
    }
}