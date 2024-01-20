package it.unibz.krdb.kprime.domain.cmd;

import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.support.CappedQueue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import it.unibz.krdb.kprime.support.substring
import org.apache.commons.io.input.ReversedLinesFileReader
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.ArrayList


sealed class CommandEvent {
    abstract fun toLog(): String
}

class CommandResponseEvent(
    val time: LocalDateTime,
    val author: String,
    private val commandId: String,
    val result: String,
    val warn: String,
    val errors: String,
    val oid: String = ""
) : CommandEvent() {


    override fun toString(): String {
        return prefix + time.format(dateTimeFormatter) + "_" + author + "> " + commandId.padEnd(50, ' ') +
                System.lineSeparator() +
                "$result warning='$warn' error='$errors'" + System.lineSeparator()
    }

    override fun toLog(): String {
        return prefix + time.format(dateTimeFormatter) + "_" + author + "> " + commandId +
                " result='$result' warning='$warn' error='$errors' oid='$oid'"
    }

    companion object {
        const val prefix = "(response):"
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HHmmss")
        fun fromLog(line: String): CommandResponseEvent {
            val time = LocalDateTime.parse(line.substring(prefix, "_"), dateTimeFormatter)
            val author = line.substring("_", ">")
            val commandId = line.substring("> ", " result=").trim()
            val result = line.substring("result='", "' warning=")
            val warning = line.substring("warning='", "' error='")
            val error = line.substring(" error='", "' oid='")
            val oid = line.substring(" oid='", "'")
            return CommandResponseEvent(time, author, commandId, result, warning, error, oid)
        }

    }

}

class CommandRequestEvent(
    val time: LocalDateTime,
    val author: String,
    val command: String,
    val oid: String = "",
    val contextName: String = ""
) : CommandEvent() {


    override fun toString(): String {
        return prefix + time.format(dateTimeFormatter) + "_" + author + "> " + command.padEnd(50, ' ') +
                System.lineSeparator()
    }

    override fun toLog(): String {
        return prefix + time.format(dateTimeFormatter) + "_" + author + "> " + command + " oid='$oid'"
    }

    companion object {
        const val prefix = "(request):"
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HHmmss")
        fun fromLog(line: String): CommandRequestEvent {
            val time = LocalDateTime.parse(line.substring(prefix, "_"), CommandResponseEvent.dateTimeFormatter)
            val author = line.substring("_", ">")
            val command = line.substring("> ", " oid=").trim()
            val oid = line.substring(" oid='", "'")
            return CommandRequestEvent(time, author, command, oid)
        }

    }
}


class CmdLoggerService {

    var commandEventLog = CappedQueue<CommandEvent>(100)

    init {
        readCommandsStory()
    }

    fun getCommandEvents(): List<CommandEvent> {
        if (commandEventLog.isEmpty()) readCommandsStory()
        return commandEventLog.toList()
    }

    private fun readCommandsStory(linesToRead: Int = 10) {
        val logCompleteFileName = SettingService.getClassInstanceDir() + "logs/commands.log"
        if (!File(logCompleteFileName).exists()) return
        val linesRead = readLastLine(File(logCompleteFileName), linesToRead)
//                List<String> = File(logCompleteFileName).bufferedReader()
//            .useLines { lines: Sequence<String > -> lines.take(linesToRead).toList() }
        for (line in linesRead) {
            if (line.startsWith(CommandRequestEvent.prefix)) commandEventLog.add(CommandRequestEvent.fromLog(line))
            if (line.startsWith(CommandResponseEvent.prefix)) commandEventLog.add(CommandResponseEvent.fromLog(line))
        }

    }

    private fun readLastLine(file: File, numLastLineToRead: Int): List<String> {
        val result: MutableList<String> = ArrayList()
        try {
            ReversedLinesFileReader(file, StandardCharsets.UTF_8).use { reader ->
                var line: String
                while (reader.readLine().also { line = it } != null && result.size < numLastLineToRead) {
                    result.add(line)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }


    fun logCommandRequest(
        context: CmdContext,
        author: String,
        cmd: TraceCmd?,
        commandLine: String
    ): TraceCmd? {
        //logger?.info("author:$author | cmd:${cmd?.getCmdName()} | topic:${cmd?.getCmdTopics()} | line:$commandLine")
        val event =
            CommandRequestEvent(LocalDateTime.now(), author, commandLine, oid = "", context.env.prjContextName.value)
        commandEventLog.add(event)
        writeCommandsStory(event)
        return cmd
    }

    fun logCommandResponse(context: CmdContext, commandLine: String, response: TraceCmdResult) {
        val event = CommandResponseEvent(
            LocalDateTime.now(), context.env.author, commandLine,
            if (response.isOK()) "SUCCESS" else "FAILURE", response.warning, response.failure,
            oid = response.oid
        )
        commandEventLog.add(event)
        writeCommandsStory(event)
    }

    private fun writeCommandsStory(event: CommandEvent) {
        val logCompleteFileName = SettingService.getClassInstanceDir() + "logs/commands.log"
        val log = File(logCompleteFileName)
        if (!log.exists()) log.createNewFile()
        log.appendText(event.toLog() + System.lineSeparator())
    }


}
