package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.check.TraceCmdCheckFact
import it.unibz.krdb.kprime.domain.cmd.create.*
import it.unibz.krdb.kprime.domain.cmd.delete.*
import it.unibz.krdb.kprime.domain.cmd.expert.*
import it.unibz.krdb.kprime.domain.cmd.rdf.TraceCmdImportOntouml
import it.unibz.krdb.kprime.domain.cmd.rdf.TraceCmdImportTTL
import it.unibz.krdb.kprime.domain.cmd.rdf.TraceCmdReadOntouml
import it.unibz.krdb.kprime.domain.cmd.rdf.TraceCmdSparqlLabel
import it.unibz.krdb.kprime.domain.cmd.read.*
import it.unibz.krdb.kprime.domain.cmd.rest.TraceCmdCallHttp
import it.unibz.krdb.kprime.domain.cmd.sql.*
import it.unibz.krdb.kprime.domain.cmd.update.*
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.support.substring
import org.apache.commons.io.input.ReversedLinesFileReader
import org.apache.logging.log4j.Logger
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


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

class CmdParser(val logger: Logger? = null) {

    var commandEventLog = mutableListOf<CommandEvent>()
    var changesetLog = mutableListOf<ChangeSet>()

    val commandList = listOf(
        TraceCmdAddGoal,
        TraceCmdRemGoal,
        TraceCmdGetGoal,
        TraceCmdSetGoal,

        TraceCmdStory,
        TraceCmdStoryNote,
        TraceCmdListStories,
        TraceCmdAddStory,

        TraceCmdListContexts,
        TraceCmdAddPrjContext,
        TraceCmdGetPrjContext,
        TraceCmdRemPrjContext,
        TraceCmdSetPrjContext,

        TraceCmdFromTemplate,

        TraceCmdAddActor,
        TraceCmdGetActor,
        TraceCmdRemActor,

        TraceCmdGetMapping,
        TraceCmdListMappings,

        TraceCmdAddChangeset,


        TraceCmdAddDoubleInc,
        TraceCmdAddDoubleFd,
        TraceCmdAddInclusion,
        TraceCmdAddFunctional,
        TraceCmdAddMultivalued,
        TraceCmdAddForeignKey,
        TraceCmdAddKey,
        TraceCmdRemConstraint,

        TraceCmdAddTable,
        TraceCmdRemTable,
        TraceCmdReplaceTable,
        TraceCmdGetTable,

        TraceCmdSetColumn,

        TraceCmd3nf,
        TraceCmdBcnf,
        TraceCmdCheckFunctionals,

        TraceCmdTraceSource,
        TraceCmdTraceDb,
        TraceCmdTraceCs,

        TraceCmdSetConnection,

        TraceCmdSqlCreate(),
        TraceCmdSqlInsert(),
        TraceCmdSqlAlter(),
        TraceCmdSqlDelete(),
        TraceCmdSqlUpdate(),
        TraceCmdSqlDrop(),
        TraceCmdSqlSelect,
        TraceCmdSqlSelectFunctionals,
        TraceCmdSqlShow6NF,
        TraceCmdSqlShow6NFSelect,
        TraceCmdSqlShowOID,
        TraceCmdSqlShowChange,
        TraceCmdSqlShowChangeLB,
        TraceCmdSqlShowViews,
        TraceCmdSqlExecCS,
        TraceCmdSqlExecCSMappings,
        TraceCmdSqlCreateTableFromCsv,
        TraceCmdSqlCreateTableFromCsvNote,
        TraceCmdSqlBatch,
        TraceCmdJsonToSql,
        TraceCmdJsonToTable,
        TraceCmdImportSQL,

        TraceCmdSelectXml,

        TraceCmdRenameMapping,
        TraceCmdRemMapping,
        TraceCmdSetMapping,

        TraceCmdCSAddTable,
        TraceCmdCSAddColumn,
        TraceCmdCSRemTable,
        TraceCmdCSAddFunctional,
        TraceCmdCSAddMapping,
        TraceCmdCSAddKey,
        TraceCmdCSAddDoubleInclusion,
        TraceCmdCSAddInclusion,
        TraceCmdCSAddForeignKey,
        TraceCmdCSDelTable,
        TraceCmdCSDelMapping,
        TraceCmdCSAddSelf,
        TraceCmdCSApply,
        TraceCmdCSClean,
        TraceCmdCSOids,

        TraceCmdGetDatabase,
        TraceCmdDatabaseClean,

        TraceCmdCurrentChangeset,
        TraceCmdGetChangeset,
        TraceCmdSaveChangeset,
        TraceCmdXmlChangeset,
        TraceCmdToChangeSet,

        TraceCmdReidentify,

        TraceCmdSaveDatabaseAsXmlSource,

        TraceCmdListAll,
        TraceCmdListConstraints,
        TraceCmdListDoubleIncs,
        TraceCmdListFacts,
        TraceCmdListFactsTurtle,
        TraceCmdListForeigns,
        TraceCmdListFunctionals,
        TraceCmdListKeys,
        TraceCmdListTables,
        TraceCmdListParams,
        TraceCmdListGoals,
        TraceCmdListTerms,
        TraceCmdListExperts,
        TraceCmdListSources,
        TraceCmdListDrivers,
        TraceCmdListActors,

        TraceCmdCheckFact,

        TraceCmdHelpCsdp,
        TraceCmdHelpDdd,
        TraceCmdHelpEventstorm,
        TraceCmdHelpAggregate,

        TraceCmdSetAsActive,
        TraceCmdCallHttp,
        TraceCmdCallExpert,
        TraceCmdSetFacttype,
        TraceCmdSetDatatype,
        TraceCmdDatatypes,
        TraceCmdTermtypes,
        TraceCmdAddSelf,
        TraceCmdAddLabel,
        TraceCmdAddLabelInstance,
        TraceCmdFindLabel,
        TraceCmdSparqlLabel,
        TraceCmdRemLabel,
        TraceCmdRemLabelInstance,
        TraceCmdRemContextNamespace,
        TraceCmdRemInstanceNamespace,
        TraceCmdListLabels,
        TraceCmdRenameTrace,
        TraceCmdAddDatabase,
        TraceCmdAddTrace,

        TraceCmdImportTTL,
        TraceCmdImportOntouml,
        TraceCmdReadOntouml,

        TraceCmdFindParagraphs,
        TraceCmdFindText,
        TraceCmdFindTerms,
        TraceCmdFindWord,

        TraceCmdAddDoc,
        TraceCmdListTemplates,
        TraceCmdRemTrace,
        TraceCmdCurrentTrace,
        TraceCmdCurrentDatabase,
        TraceCmdListTraces,

        TraceCmdUseSource,
        TraceCmdGetSource,
        TraceCmdAddSource,
        TraceCmdRemSource,
        TraceCmdCurrentSource,
        TraceCmdFromSource,
        TraceCmdSetDbName,
        TraceCmdXmlDatabase,
        TraceCmdNewDb,
        TraceCmdNewH2,

        TraceCmdAddFact(),
        TraceCmdAddFactShortcut(),
        TraceCmdQueryFact,

        TraceCmdAddTerm,
        TraceCmdGetTerm,
        TraceCmdRemTerm,
        TraceCmdSetTerm,

        TraceCmdAddMapping,
        //TraceCmdEval,
        TraceCmdSaveDatabase,
        TraceCmdMeta,
        TraceCmdMetaToDatabase,
        TraceCmdSuggest,
        TraceCmdValidate,
        TraceCmdAddParam,
        TraceCmdRemParams,
        TraceCmdTransApply,
        TraceCmdTransApplicability,
        TraceCmdSetColDbType,
        TraceCmdGoalsStatus,
        TraceCmdAddVocabulary,
        TraceCmdListVocabularies,
        TraceCmdCreateSource,
        TraceCmdCreateExpert,
        TraceCmdDropExpert,
        TraceCmdRenameExpert,
        TraceCmdTraceGoal,
        TraceCmdVuca,
    )
    private val commandNameList = commandList.map { cmd -> cmd.getCmdName() }
    private val commandMap = commandList.associateBy { cmd -> cmd.getCmdName() }

    private fun isNotTest(): Boolean = logger != null

    fun parse(command: String, context: CmdContext): Pair<TraceCmdResult, CmdContext> {
        if (isNotTest() && commandEventLog.isEmpty()) {
            readCommandsStory()
        }
        return when (command) {
            "" -> parseEmptyCommand(context, command)
            "log" -> parseLogMetaCommand(context)
            "undo" -> parseUndoMetaCommand(context)
            else -> parseCommand(command, context)
        }
    }

    internal fun commandName(command: String) = command.trim().split(" ")[0].lowercase(Locale.getDefault())

    private fun parseCommand(command: String, context: CmdContext): Pair<TraceCmdResult, CmdContext> {
        val commandName = commandName(command)
        val response: TraceCmdResult = try {
            if (isAKnownCommand(commandName)) {
                executeCommand(commandName, context, command)
            } else giveHelp(commandName, context, command)
        } catch (e: Exception) {
            e.printStackTrace()
            TraceCmdResult() failure e.message.toString()
        }
        if (response.isOK()) {
            if (isMutatorCmd(commandName)) {
                if (isNotTest()) saveTermbaseWithPrevious(context)
                val datetimePattern = DateTimeFormatter.ofPattern("M/d/y H:m:ss")
                context.env.changeSet.time = LocalDateTime.now().format(datetimePattern)
                //val retime = LocalDateTime.parse(context.env.changeSet.time,datetimePattern)
                //println(retime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)))
                context.env.changeSet.commands?.add(command)
            }
        }
        if (isNotTest()) logCommandResponse(context, command, response)
        return Pair(response, context)
    }

    /*
    private fun parseChangeSetExecMetaCommand(context: CmdContext, command: String): Pair<TraceCmdResult, CmdContext> {
        val commands = context.env.changeSet.commands ?: emptyList()
        commands.toList().forEach {
            val cmd = it.toString()
            val cmdName = cmd.split(" ")[0]
            executeCommand(cmdName, context, cmd)
        }
        return Pair(TraceCmdResult() message "changeset executed", context)
    }
     */

    private fun parseUndoMetaCommand(context: CmdContext): Pair<TraceCmdResult, CmdContext> {
        val undone = undoCommand(context)
        return Pair(TraceCmdResult() message "undo $undone", context)
    }

    private fun parseLogMetaCommand(context: CmdContext): Pair<TraceCmdResult, CmdContext> {
        return Pair(
            TraceCmdResult() message commandEventLog.reversed().take(5)
                .joinToString(separator = System.lineSeparator()), context
        )
    }

    private fun parseEmptyCommand(context: CmdContext, command: String): Pair<TraceCmdResult, CmdContext> {
        val hint = TraceCmdSuggest.execute(context, command).message
        return Pair(TraceCmdResult() message hint options commandNameList, context)
    }

    private fun isAKnownCommand(commandName: String) = commandMap[commandName] != null

    private fun giveHelp(
        commandName: String,
        context: CmdContext,
        command: String
    ): TraceCmdResult {
        return when (commandName) {
            "?" -> TraceCmdSuggest.execute(context, command)
            "??" -> respondWithCompleteCommandList()
            "help" -> respondWithHelp(command)
            else -> TraceCmdResult() failure "Command $commandName unknown." options commandNameList
        }
    }

    private fun respondWithHelp(command: String): TraceCmdResult {
        val tokens = command.split(" ")
        if (tokens.size == 1) {
            val helpHint = """
                To have list of command topics give the command 'help topic'.
                To have filter command list by topic give the command 'help topic <topic>'.  
            """.trimIndent()
            return TraceCmdResult() message helpHint
        }
        if (tokens.size == 2 && tokens[1] == "topic") return topicList()
        if (tokens.size > 2 && tokens[1] == "topic") return respondWithCommandListFiltered(command)
        return respondWithCompleteCommandList()
    }

    private fun respondWithCommandListFiltered(command: String): TraceCmdResult {
        val topics = command.split(" ").drop(2)
        var responseMessage = ""
        for (cmd in commandList) {
            if (cmd.getCmdTopics().contains(topics.first()))
                responseMessage += "%-10s\t%s\t%s%n".format(
                    cmd.getCmdName(),
                    cmd.getCmdDescription(),
                    cmd.getCmdTopics()
                )
        }
        return TraceCmdResult() message responseMessage
    }

    private fun topicList(): TraceCmdResult {
        var result = ""
        var topicSet = setOf<String>()
        for (command in commandList) {
            topicSet = topicSet.plus(command.getCmdTopics().split(","))
        }
        result += topicSet.joinToString(System.lineSeparator())
        return TraceCmdResult() message result
    }

    private fun executeCommand(
        commandName: String,
        context: CmdContext,
        commandLine: String
    ): TraceCmdResult {
        val cmd = commandMap[commandName]
            ?: return TraceCmdResult() failure "No command [$commandName] found."
        val executeResult: TraceCmdResult = try {
            if (isNotTest()) logCommandRequest(context, context.env.author, cmd, commandLine)
            cmd.execute(context, commandLine)
        } catch (eia: Throwable) {
            eia.printStackTrace()
            TraceCmdResult() failure (eia.message ?: "Illegal argument or checked state.")
        }
        writeDatabaseIfTopicRequired(cmd, executeResult, context)
        return executeResult
    }

    private fun writeDatabaseIfTopicRequired(
        cmd: TraceCmd,
        executeResult: TraceCmdResult,
        context: CmdContext
    ) {
        if (cmd.getCmdTopics().contains(TraceCmd.Topic.WRITE.name)
            && cmd.getCmdTopics().contains(TraceCmd.Topic.DATABASE.name)
            && executeResult.isOK()
        ) {
            context.pool.dataService.writeDatabase(
                context.env.database,
                context.env.prjContextLocation,
                TraceName(".kprime/traces/" + context.env.currentTrace.value)
            )
            println("Written database [${context.env.prjContextLocation.value}][${context.env.currentTrace.value}]")
        }
    }

    private fun logCommandRequest(
        context: CmdContext,
        author: String,
        cmd: TraceCmd?,
        commandLine: String
    ): TraceCmd? {
        //logger?.info("author:$author | cmd:${cmd?.getCmdName()} | topic:${cmd?.getCmdTopics()} | line:$commandLine")
        val event = CommandRequestEvent(LocalDateTime.now(), author, commandLine, oid = "",context.env.prjContextName.value)
        commandEventLog.add(event)
        writeCommandsStory(event)
        return cmd
    }

    private fun logCommandResponse(context: CmdContext, commandLine: String, response: TraceCmdResult) {
        val event = CommandResponseEvent(
            LocalDateTime.now(), context.env.author, commandLine,
            if (response.isOK()) "SUCCESS" else "FAILURE", response.warning, response.failure,
            oid = response.oid
        )
        commandEventLog.add(event)
        writeCommandsStory(event)
    }

    private fun isMutatorCmd(cmdName: String) =
        !cmdName.startsWith("changeset") &&
                commandMap[cmdName]?.getCmdTopics()?.contains("write") == true

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

    fun getCommandEvents(): List<CommandEvent> {
        if (commandEventLog.isEmpty()) readCommandsStory()
        return commandEventLog
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

    private fun writeCommandsStory(event: CommandEvent) {
        val logCompleteFileName = SettingService.getClassInstanceDir() + "logs/commands.log"
        val log = File(logCompleteFileName)
        if (!log.exists()) log.createNewFile()
        log.appendText(event.toLog() + System.lineSeparator())
    }

    private fun undoCommand(context: CmdContext): String {
        val traceName = context.env.currentTrace
        val lastTermBaseName = File(context.pool.settingService.getTraceDir(traceName)).list().orEmpty()
        println(lastTermBaseName.toList())
        println(context.env.currentTraceFileName)
        val lastTermBaseName2 = lastTermBaseName.filter { it.startsWith(context.env.database.name ) }
        println(lastTermBaseName2)
        val lastTermBaseName3 = lastTermBaseName2.sortedDescending().drop(1).firstOrNull() ?: ""
        println(lastTermBaseName3)
        if (lastTermBaseName3.isEmpty()) return "No rollback file found."

        val previousTermbaseString =
            File(context.pool.settingService.getTraceDir(traceName) + lastTermBaseName3).readText()
        val previousTermbase = context.pool.dataService.databaseFromXml(previousTermbaseString)
        context.env.database = previousTermbase

        // drop current database file
        File(context.pool.settingService.getTraceDir(traceName) + previousTermbase.name + "_db.xml").delete()

        // rename previous as current file
        File(context.pool.settingService.getTraceDir(traceName) + lastTermBaseName3).renameTo(
            File(context.pool.settingService.getTraceDir(traceName) + previousTermbase.name + "_db.xml")
        )

        return "Reloaded $lastTermBaseName3"
    }

    private fun respondWithCompleteCommandList(): TraceCmdResult {
        var responseMessage = ""
        for (cmd in commandList) {
            responseMessage += "%-10s\t%s\t%s%n".format(cmd.getCmdName(), cmd.getCmdDescription(), cmd.getCmdTopics())
        }
        return TraceCmdResult() message responseMessage
    }

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS")

    private fun saveTermbaseWithPrevious(context: CmdContext) {
        val database: Database = context.env.database
//        val sourcesDir: String = context.settingService.getSourcesDir()
//        val sourceService: SourceService = context.sourceService
        val databaseContent = context.pool.dataService.prettyDatabase(database)
        val traceFileName = context.getCurrentTraceDir() + "/" + database.name + "_db.xml"
        val timestamp = timeFormatter.format(LocalDateTime.now())
        val traceFileNameRevisioned = context.getCurrentTraceDir() + "/" + database.name + "_${timestamp}_db.xml"
        context.env.currentTraceFileName = database.name
        val fileInTrace = File(traceFileName)
        if (fileInTrace.exists()) {
            fileInTrace.copyTo(File(traceFileNameRevisioned), overwrite = true)
            fileInTrace.writeText(databaseContent)
        }
    }

    fun suggestions(command: String): TraceCmdResult {
        return commandMap[commandName(command)]?.getCmdSuggestions()
            ?: (TraceCmdResult() options commandNameList)
    }
}