package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import java.io.File

object TraceCmdListTraces: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "list-traces"
    }

    override fun getCmdDescription(): String {
        return "list all traces"
    }

    override fun getCmdUsage(): String {
        return "usage: list-traces"
    }

    override fun getCmdTopics(): String {
        return "read,logical,trace"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val workingTracesDir = context.pool.settingService.getWorkingDir() + "/traces"
        if (!File(workingTracesDir).isDirectory) return TraceCmdResult() failure "Traces dir is not defined."
        return TraceCmdResult() message listFolderNamesInFolder(workingTracesDir).joinToString(" "+System.lineSeparator())
    }

    private fun listFolderNamesInFolder(dir: String): List<String> {
        try {
            return File(dir).listFiles()
                    .filter { f -> f.isDirectory }
                    .map { f -> f.name }.toList()
        } catch (e: Exception ) {
            return emptyList()
        }
    }

    fun listTracesNames(context: CmdContext): List<String> {
        val workingTracesDir = context.pool.settingService.getWorkingDir() + "/traces"
        if (!File(workingTracesDir).exists()) {
            File(context.pool.settingService.getWorkingDir() + "/traces").mkdir()
        }
        val listFolderNamesInFolder = listFolderNamesInFolder(workingTracesDir).toMutableList()
        if (!listFolderNamesInFolder.contains("root") || listFolderNamesInFolder.isEmpty()) {
            File(context.pool.settingService.getWorkingDir() + "/.kprime/traces/root").mkdir()
            listFolderNamesInFolder.add("root")
        }
        return listFolderNamesInFolder
    }
}