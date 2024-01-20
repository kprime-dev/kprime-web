package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.adapter.jackson.JacksonService
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.expert.ExpertService
import it.unibz.krdb.kprime.domain.project.*
import it.unibz.krdb.kprime.domain.search.SearchService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.transformer.TransformerService
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.view.EventListener
//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import org.apache.logging.log4j.message.StringFormatterMessageFactory
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.domain.nextGid

class CmdService (
    private val settingService: SettingService,
    sourceService: SourceService,
    expertService: ExpertService,
    transformerService: TransformerService,
    termService: TermService,
    todoService: TodoService,
    prjContextService: PrjContextService,
    searchService: SearchService,
    actorService: ActorService,
    dataService: DataService,
    storyService: StoryService,
    rdfService: RdfService,
    traceService: TraceService,
    userService: UserService,
    statService: StatService
) {

        //val logger: Logger = LogManager.getLogger(StringFormatterMessageFactory.INSTANCE)

        val pool = CmdServicePool(
            settingService,
            sourceService,
            expertService,
            transformerService,
            termService,
            todoService,
            prjContextService,
            searchService,
            actorService,
            dataService,
            storyService,
            rdfService,
            traceService,
            userService,
            JacksonService(),
            statService
        )


    fun suggestions (command: String): TraceCmdResult {
        return cmdParser.suggestions(command)
    }

    @Synchronized
    // this is the only method authorized to change the current context state.
    fun parse(author: String, command: String, contextId: String = "no-context", contextName: String = "", paylaod : List<String> = emptyList()): TraceCmdResult {
        val contextIn = currentCmdContext(author,contextId,contextName, paylaod)
        println("[${contextIn.env.prjContextName.value}]:[$command]")
        if (command == "info") return TraceCmdResult() message """
                contextId  [$contextId] (if np-context uses settings current project name ${settingService.getProjectName()})
                author [$author]
                contexts [${contextMap.size}]
                commands [${cmdParser.commandList.size}]
                command logs [${cmdParser.commandEventLog.size}]
        """.trimIndent()
        if (command == "info-context") return TraceCmdResult() message """
                contextId [$contextId] 
                author [$author]
                projectLocation [${contextMap[author]?.env?.prjContextLocation}]
                currentElement [${contextMap[author]?.env?.currentElement}]
                currentTrace [${contextMap[author]?.env?.currentTrace?.value}]
                currentTraceFileName [${contextMap[author]?.env?.currentTraceFileName}]
                database.schema.tables.size [${contextMap[author]?.env?.database?.schema?.tables?.size}]
        """.trimIndent()
        val (result ,contextOut) = cmdParser.parse(command, contextIn)
        contextMap[author] = contextOut
        sentToListner(contextOut,command)
        return result
    }

    @Deprecated("old",ReplaceWith("user parseCommandEnvelops"))
    fun parseCommandsAndContextIdLines(reqBody: String): Pair<List<String>, String> {
        val lines = reqBody.substringBeforeLast("#").split(System.lineSeparator())
        val commandsAndEmpty = lines.filter { it.startsWith(">") || it.isBlank() }
        val commands = mutableListOf<String>()
        var command = ""
        for (commandOrEmpty in commandsAndEmpty) {
            command = if (commandOrEmpty.isBlank() && command.isNotBlank()) {
                commands.add(command)
                ""
            } else {
                if (command.isEmpty()) commandOrEmpty.substring(1).trim()
                else command + " " + commandOrEmpty.substring(1).trim()
            }
        }
        if (command.isNotBlank()) commands.add(command)
        val contextId = if (reqBody.contains("#")) reqBody.substringAfterLast("#") else ""
        return Pair(commands, contextId)
    }


    fun currentCmdContext(currentAuthor: String, contextId: String, contextName: String, payload: List<String>): CmdContext {
        if (contextMap[currentAuthor]!=null) {
            if (contextName==contextMap[currentAuthor]!!.env.prjContextName.value)
                return contextMap[currentAuthor]!!
        }
        val prjLocation = PrjContextLocation(pool.prjContextService.projectByName(contextName)?.location ?: PrjContext.NO_WORKING_DIR)
        println("currentTraceContext contextName [$contextName] projectLocation [${prjLocation.value}]")
//        println("currentTraceContext payload [$payload]")
            println("se è la prima chiamata crea il nuovo contesto")
            val db = pool.dataService.getDatabase(prjLocation,"root","base").getOrElse { Database().withGid() }
            println("currentTraceContext is null, new Base [ ${db.id} ]" )
        val changeSet = ChangeSet()
        changeSet.gid = nextGid()
        contextMap[currentAuthor] = CmdContext(
                CmdEnvironment(
                    changeSet = changeSet,
                    database = db,
                    author = currentAuthor,
                    datasource = null,
                    prjContextLocation = prjLocation,
                    prjContextName = PrjContextName(contextName),
                    prjContextIRI = PrjContextIRI("http://localhost:7000/"), //FIXME
                    currentParams = HashMap(),
                    currentTrace = TraceName("root"),
                    currentElement  = "",
                    currentTraceFileName = "base"
                ),
                pool,
                CmdEnvelope(emptyList(),contextId,payload)
            )
        println("ritorna il contesto in buffer")
        return contextMap[currentAuthor]?:throw IllegalStateException("contextMap[$contextId]")
    }

    // -----------------------------------------------------------------
    // websocket experimental

    companion object {

        //private val cmdParser = CmdParser(LogManager.getLogger())
        private val cmdParser = CmdParser(null,CmdLoggerService())

        private val contextMap : MutableMap<String, CmdContext> = mutableMapOf()

        private val listeners: MutableList<EventListener> = ArrayList()

        fun subscribe(listner: EventListener) {
            this.listeners.add(listner)
        }

        fun sentToListner(contextIn: CmdContext, event: String) {
            //val docId2 = "dic_" + contextIn.env.currentTrace + "_" + contextIn.env.currentTraceFileName
            //println("docId2: $docId2")
            val docId =
                "dic_" + contextIn.env.currentTrace + "_null" // FIXME i comandi non impostano contextIn.env.currentTraceFileName
            for (listener in listeners) {
                println("sentToListner(): $docId $event")
                listener.eventListen(docId, event)
                listener.eventListen("1111", event)
            }
        }

        fun getEventLog(): List<CommandEvent> {
            return this.cmdParser.getCommandEvents()
        }

        fun parseCmdEnvelop(reqBody:String):CmdEnvelope  {
            println("parseCmdEnvelop:$reqBody")
            val contextId = "" //if (reqBody.contains("#")) reqBody.substringAfterLast("#") else ""
            //val lines = reqBody.substringBeforeLast("#").split(System.lineSeparator())
            val lines = reqBody.split(System.lineSeparator())
            val commandLines = extractsCommandLines(lines)
            val payload = extractPayload(lines)
            return CmdEnvelope(commandLines,contextId,payload)
        }

        private fun extractPayload(lines: List<String>): MutableList<String> {
            val payload = mutableListOf<String>()
            var inPayload = false
            println(lines.size)
            for (line in lines.map { it.trimIndent()}) {
                if (line.startsWith("```") && inPayload) {
                    println("END PAYLOAD")
                    inPayload = false
                } else
                    if (inPayload && !line.startsWith("```")) {
                        payload.add(line)
                        println("ADD PAYLOAD")
                    } else
                        if (line.startsWith("```") && !inPayload) {
                            println("START PAYLOAD")
                            inPayload = true
                        }
            }
            return payload
        }

        private fun extractsCommandLines(lines: List<String>): MutableList<String> {
            val commandsAndEmpty = lines.filter { it.startsWith(">") || it.isBlank() }
            val commandLines = mutableListOf<String>()
            var command = ""
            for (commandOrEmptyLine in commandsAndEmpty) {
                command = if (commandOrEmptyLine.isBlank() && command.isNotBlank()) {
                    commandLines.add(command)
                    ""
                } else {
                    if (commandOrEmptyLine.isNotEmpty()) {
                        if (command.isEmpty()) commandOrEmptyLine.substring(1).trim()
                        else command + " " + commandOrEmptyLine.substring(1).trim()
                    } else ""
                }
            }
            if (command.isNotBlank()) commandLines.add(command)
            return commandLines
        }

    }

}