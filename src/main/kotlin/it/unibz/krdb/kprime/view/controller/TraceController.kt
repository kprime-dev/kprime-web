package it.unibz.krdb.kprime.view.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.cs.semint.kprime.expert.adapter.ExpertPayload
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.cmd.CmdService
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.trace.TraceName
import org.apache.http.entity.ContentType
import java.io.File
import java.io.FileNotFoundException
import javax.servlet.http.HttpServletResponse

class TraceController(
    private val cmdService: CmdService,
    val traceService: TraceService,
    val prjContextService: PrjContextService,
    val dataService: DataService
)  {

    val getDocument = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val projectLocation = prjContextService.projectByName(projectName)?.location?:""
        val traceFileContent = traceService.getTraceFileContent(traceName,traceFileName,projectLocation)
        ctx.html(traceFileContent)
        ctx.contentType("text/markdown")
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val playTraceFile = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val traceContent = traceService.getTraceFileContent(traceName,traceFileName)
        traceService.setTraceContext(traceName,traceFileName,traceContent)
        val split = traceContent.split(System.lineSeparator())
        val result = mutableListOf<String>()
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val author : String = currentUserName ?: "nouser"
        for (line in split) {
            if (line.startsWith(">")) {
                val parse = cmdService.parse(author, line.drop(1),"ID_$contextName",contextName)
                result.add(parse.message)
                result.add(parse.warning)
                result.add(parse.failure)
            }
        }
        val content = result.filter { line -> line.trim().isNotEmpty() }.joinToString("</textarea><hr class=\"green\"><textarea  cols=\"120\" rows=\"10\">")
        val htmlResult = """
  <style>
.red {
  border-top: 2px dashed red;
}  
.green {
  border-top: 2px dashed ;
  color: #0F0;
}  
  </style>
<pre><textarea cols="120" rows="10">$content</textarea></pre>            
        """.trimIndent()
        ctx.html(htmlResult)
    }

    val putTraceFile =  Handler { ctx ->
        println("--------------- putTraceFile:")
        val fileContent= ctx.body()
        val traceName = ctx.pathParam("traceName").replace("___","/")
        val traceFileName = ctx.pathParam("traceFileName")
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            try {
                traceService.putTraceFile(project.location, traceName, traceFileName, fileContent)
                ctx.status(HttpServletResponse.SC_ACCEPTED)
            } catch (exception:Exception) {
                ctx.json(TraceCmdResult() failure exception.localizedMessage)
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
            }
        }

    }

    val putDocFile =  Handler { ctx ->
        val expertPayload = jsonMapper().readValue(ctx.body(), ExpertPayload::class.java)
        println("--------------- putDocFile:")
        println(expertPayload.toString())
        val fileContent= expertPayload.result as String
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName").replace("___","/")
        val traceFileName = ctx.pathParam("traceFileName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            try {
                traceService.putTraceFile(project.location, traceName, traceFileName, fileContent)
                println("--------------- PUT DOC ACCEPTED")
                ctx.status(HttpServletResponse.SC_ACCEPTED)
            } catch (exception: Exception) {
                println("--------------- PUT DOC SC_BAD_REQUEST ${exception.localizedMessage}")
                exception.printStackTrace()
                ctx.json(TraceCmdResult() failure exception.localizedMessage)
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
            }
        }
    }

    val getTraceDatabase=  Handler { ctx : Context ->
        val traceName = ctx.pathParam("traceName")
        ctx.json(traceService.getsetTraceDatabaseContent(traceName))
    }

    val playCommands= Handler { ctx :Context ->
        val contextName = ctx.pathParam("contextName")
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val reqBody = ctx.body()
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        val author: String = currentUserName ?: "no-user"
        println("playCommands request author: $author")
        val resultList = mutableListOf<TraceCmdResult>()
        for (commandLine in cmdEnvelope.cmdLines) {
            println("WEB-command: [[$commandLine]]  ")
            val result = cmdService.parse(author, commandLine, cmdEnvelope.cmdContextId, contextName, cmdEnvelope.cmdPayload)
            println("WEB-result: [[${result.message}]]")
//            result.message = result.message.replace('<','_')
//            result.message = result.message.replace('>','_')
//            result.message = result.message.replace("\n","<BR>")
            resultList.add(result)
            if (!result.isOK()) break
        }
        ctx.json(resultList)
    }

    val playApiCommands = Handler { ctx :Context ->
        val contextName = ctx.pathParam("contextName")
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val reqUserName = ctx.header("kpuser")
        val reqUserPass = ctx.header("kppass")
        if (! cmdService.pool.userService.authenticate(reqUserName,reqUserPass)) {
            ctx.json(TraceCmdResult() failure "Not Authorized")
            ctx.status(HttpServletResponse.SC_UNAUTHORIZED)
        } else {
            val reqBody = ctx.body()
            println()
            println()
            println()
            println("API-request:[[$reqBody]]")
            val (commandLines, contextId) = cmdService.parseCommandsAndContextIdLines(reqBody)
            val author: String = currentUserName ?: reqUserName ?: "no-user"
            println("playCommands request author: $author")
            val resultList = mutableListOf<TraceCmdResult>()
            for (commandLine in commandLines) {
                println("API-command: [[$commandLine]]")
                val result = cmdService.parse(author, commandLine, contextId, contextName)
                println("API-result: [[${result.message}]]")
                resultList.add(result)
                if (!result.isOK()) break
            }
            ctx.json(resultList)
        }
    }

    val playApiSuggestions = Handler { ctx :Context ->
        val contextName = ctx.pathParam("contextName")
        val commandLine = ctx.pathParam("command")
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val author: String = currentUserName ?: "no-user"
        val contextId = ""
        println("---headerMap---")
        println(ctx.headerMap())
        println("-----------")
        val reqUserName = ctx.header("kpuser")
        val reqUserPass = ctx.header("kppass")
        val contextLocation = cmdService.pool.prjContextService.projectByName(contextName)?.location ?: ""
//        if (! cmdService.pool.actorService.allActors(contextLocation).onSuccess { it.filter { it.pass==reqUserPass } }.isSuccess) {
//            ctx.json(TraceCmdResult() failure "Not Authorized")
//            ctx.status(HttpServletResponse.SC_UNAUTHORIZED)
//        } else {
            val result = cmdService.suggestions(commandLine)
            println("API-result: [[${result.message}]]")
            ctx.json(result)
//        }
    }

    val postFile = Handler { ctx :Context ->
        val contextName = ctx.pathParam("contextName")
        val fileName = ctx.pathParam("fileName")
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val fileContent = ctx.body()
        println(fileContent)
        val contextLocation = cmdService.pool.prjContextService.projectByName(contextName)?.location ?: ""
        File(contextLocation+fileName).writeBytes(ctx.bodyAsBytes())
    }

    val getFile = Handler { ctx :Context ->
        val contextName = ctx.pathParam("contextName")
        val fileName = ctx.pathParam("fileName")
        val currentUserName = ctx.sessionAttribute<String>("currentUser")
        val contextLocation = cmdService.pool.prjContextService.projectByName(contextName)?.location ?: ""
        println("getFile:$contextLocation$fileName")
        try {
            ctx.result(File(contextLocation + fileName).inputStream())
        } catch (ex : FileNotFoundException) {
            println(ex.message)
            ctx.res.sendError(404)
        }
    }

    val getTraceSource = Handler { ctx: Context ->
        val sourceName = ctx.pathParam("sourceName")
        traceService.traceSource(sourceName)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getTraceGoal = Handler { ctx: Context ->
        val goalName = ctx.pathParam("goalName")
        traceService.traceGoal(goalName)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var getTraces = Handler { ctx : Context ->
        ctx.json(traceService.getTraces())
    }

    var getProjectTraces = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            ctx.json(traceService.getProjectTraces(project.location,""))
            ctx.status(HttpServletResponse.SC_OK)
        }
    }

    var getProjectSubTraces = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName").replace("___","/")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            ctx.json(traceService.getProjectTraces(project.location,traceName))
            ctx.status(HttpServletResponse.SC_OK)
        }
    }

    var getProjectStories = Handler { ctx: Context ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            ctx.json(traceService.getProjectTraceFiles(project.location,traceName.toDirName()))
            ctx.status(HttpServletResponse.SC_OK)
        }
    }

    var getTraceFiles = Handler { ctx : Context ->
        val traceName = ctx.pathParam("traceName")
        traceService.setCurrentTrace(traceName)
        ctx.json(traceService.getTraceFileNames(traceName))
    }

    var deleteTraceFiles = Handler { ctx : Context ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        if (traceService.deleteTraceDir(contextName,traceName.toDirName()))
            ctx.status(HttpServletResponse.SC_ACCEPTED)
        else
            ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
    }

    var deleteTraceFileContent = Handler { ctx : Context ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        if (traceService.deleteTraceFile(contextName,traceName,traceFileName))
            ctx.status(HttpServletResponse.SC_ACCEPTED)
        else
            ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
    }

    var notebook = Handler { ctx : Context ->
        ctx.redirect("/noteedit.html")
    }

    var getTraceTransfApplicability = Handler { ctx: Context ->
        val traceName = ctx.pathParam("traceName")
        ctx.json(traceService.getTraceTransApplicability(traceName))
    }

    val getTraceTransApply =  Handler { ctx ->
        val traceName = ctx.pathParam("traceName")
        val transformerName = ctx.pathParam("transformerName")
        ctx.json(traceService.applyTransformer(traceName,transformerName))
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getTraceTree = Handler { ctx ->
        val treeData = traceService.getTracesTree()
        ctx.json(treeData)
    }

    val databaseFactsDescription = {
//TODO        FactDescriptor().describe(traceService.getContextEnv(),"")
        ""
    }

    val envJson = {
//        val mapper = JsonMapper.builder().addModule(JavaTimeModule()).build()
//TODO        mapper.writeValueAsString(traceService.getContextEnv())
        ""
    }

    val eventLogJson = {
//        val mapper = JsonMapper.builder().addModule(JavaTimeModule()).build()
//TODO        mapper.writeValueAsString(traceService.getEventLog().takeLast(10))
        ""
    }

    var getEventLog = Handler { ctx : Context ->
        val mapper = jacksonMapperBuilder().addModule(JavaTimeModule()).build()
        val lastLogs = mapper.writeValueAsString(CmdService.getEventLog())//.takeLast(10)
        ctx.result(lastLogs)
        ctx.contentType(ContentType.APPLICATION_JSON.mimeType)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

}
