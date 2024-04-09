package it.unibz.krdb.kprime.domain.cmd.system

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.support.CommandExecutor

object TraceCmdExecute: TraceCmd {

    override fun getCmdName(): String {
        return "execute"
    }

    override fun getCmdDescription(): String {
        return "Execute a system command using hosting process privileges."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.PHYSICAL,
            TraceCmd.Topic.SYSTEM).joinToString()
    }

    private enum class ArgNames { command }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentFreeText(ArgNames.command.name,"command to execute",2,200, pattern = "") required true,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val commandToExecute =  args[ArgNames.command.name]  as String
        val executionResult = CommandExecutor().executeProcess(commandToExecute)
        return TraceCmdResult() message  executionResult
    }

}