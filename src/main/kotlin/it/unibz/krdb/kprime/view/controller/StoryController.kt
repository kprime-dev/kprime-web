package it.unibz.krdb.kprime.view.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.json.jsonMapper
import it.unibz.cs.semint.kprime.expert.adapter.ExpertPayload
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.story.StoryService.StoryServiceError
import it.unibz.krdb.kprime.domain.cmd.CmdService
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdFromTemplate
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListTemplates
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.story.StoryName
import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.trace.TraceFileName
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.user.User
import org.apache.http.entity.ContentType
import javax.servlet.http.HttpServletResponse

@JvmInline
value class Notebook (val notes:List<StoryController.Note>)

class StoryController(private val cmdService: CmdService,
                      val storyService: StoryService
) {

    val getTemplates = Handler { ctx ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val contextName = ContextName.NO_CONTEXT_NAME
        val result = cmdService.parse(
            currentUser, "${TraceCmdListTemplates.getCmdName()} ", contextName = contextName.value
        )
        ctx.json(result.payload?:"")
    }

    var putTemplate = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val contextName = ContextName(ctx.pathParam("contextName"))
        val storyName = StoryName(ctx.pathParam("storyName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val templateName = TemplateName(ctx.pathParam("templateName"))
        val result = cmdService.parse(
            currentUser, "${TraceCmdFromTemplate.getCmdName()} " +
                    "${templateName.value} " +
                    "${storyName.value} " +
                    "${traceName.toUrlName()}", contextName = contextName.value
        )
        if (!result.isOK()) ctx.status(HttpServletResponse.SC_BAD_REQUEST)
        ctx.json(result)
    }

    var getProjectStoryPage = Handler { ctx: Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName("traces___root")//TraceName(ctx.pathParam("traceName"))
        val storyName = StoryName(ctx.pathParam("traceFileName"))
        ctx.redirect("/noteview.html?pr=${projectName.value}&tr=${traceName.value}&tf=${storyName.value}.md")
    }

    var getProjectFile = Handler { ctx: Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = TraceFileName(ctx.pathParam("traceFileName"))
        cmdService.pool.traceService.getTraceFile(projectName,traceName,traceFileName)
            .onFailure { error ->
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
                ctx.json(error.message?:"getProjectFile error")
            }
            .onSuccess { file ->
                if (traceFileName.toString().endsWith(".jpg"))
                    ctx.contentType(ContentType.IMAGE_JPEG.mimeType)
                if (traceFileName.toString().endsWith(".png"))
                    ctx.contentType(ContentType.IMAGE_PNG.mimeType)
                if (traceFileName.toString().endsWith(".gif"))
                    ctx.contentType(ContentType.IMAGE_GIF.mimeType)
                ctx.result(file)
            }

    }

    var getProjectStoryEditPage = Handler { ctx: Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName("___")//TraceName("traces___root")//TraceName(ctx.pathParam("traceName"))
        val storyName = StoryName(ctx.pathParam("traceFileName"))
        val goalID = ctx.pathParam("goalID")
        ctx.redirect("/noteedit.html?pr=${projectName.value}&tr=${traceName.value}&goalid=${goalID}&tf=${storyName.value}.md")
    }

    var getGoalStoryEditPage = Handler { ctx: Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName("traces___root")//TraceName(ctx.pathParam("traceName"))
        val storyName = StoryName(ctx.pathParam("traceFileName"))
        ctx.redirect("/noteedit.html?pr=${projectName.value}&tr=${traceName.value}&tf=${storyName.value}.md")
    }

    var getProjectStoryRename = Handler { ctx: Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val oldName = StoryName(ctx.pathParam("oldName"))
        val newName = StoryName(ctx.pathParam("newName"))
            storyService.renameProjectTraceFile(projectName,traceName.toDirName(),oldName.toFileName(),newName.toFileName())
                .onSuccess {
                    ctx.json(it)
                    ctx.status(HttpServletResponse.SC_OK)
                }
                .onFailure {
                    ctx.json(it.message?:"Bad Request")
                    ctx.status(HttpServletResponse.SC_BAD_REQUEST)
                }
        }

    val getDocument = Handler { ctx ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = TraceFileName(ctx.pathParam("traceFileName"))
        val traceFileContent = storyService.getDocumentMd(projectName, traceName, traceFileName).getOrDefault("")
        ctx.html(traceFileContent)
        ctx.contentType("text/markdown")
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val putDocument =  Handler { ctx ->
        val expertPayload = jsonMapper().readValue(ctx.body(), ExpertPayload::class.java)
        println("--------------- putDocFile:")
        println(expertPayload.toString())
        val fileContent= expertPayload.result as String
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        storyService.putDocumentMd(projectName,traceName,traceFileName,fileContent)
            .onSuccess {
                println("--------------- PUT DOC ACCEPTED")
                ctx.status(HttpServletResponse.SC_ACCEPTED)
            }.onFailure { error ->
                when(error){
                    is StoryServiceError.BadRequest -> {
                        ctx.json(TraceCmdResult() failure error.mes)
                        ctx.status(HttpServletResponse.SC_BAD_REQUEST)
                    }
                    is StoryServiceError.ProjectUnknown -> {
                        ctx.status(HttpServletResponse.SC_NOT_FOUND)
                    }
                }
            }
    }


    data class Note(
        val id:Int,
        val title:String,
        val marked:String,
        val completed:Boolean,
        val commandResult:String = "",
        val commandFailure:String = "")

    var editTraceNotebook = Handler { ctx : Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = TraceFileName(ctx.pathParam("traceFileName"))

        storyService.readNotes(projectName, traceName, traceFileName, edit = true)
            .onSuccess { notes ->
                ctx.json(notes)
                ctx.status(HttpServletResponse.SC_OK)
            }.onFailure {
                ctx.status(HttpServletResponse.SC_NOT_FOUND)
            }
    }

    var writeTraceNotebook = Handler { ctx : Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = TraceFileName(ctx.pathParam("traceFileName"))
        println(ctx.body())
        val notesText = ctx.body()
        val notes =  jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(notesText,Array<StoryController.Note>::class.java)
        storyService.writeNotes(projectName, traceName, traceFileName, edit = true, 1, notes.toList())
        .onSuccess {
            ctx.json(it)
            ctx.status(HttpServletResponse.SC_OK)
        }.onFailure {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        }
        /*
         */

    }

    var readTraceNotebook = Handler { ctx : Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = TraceFileName(ctx.pathParam("traceFileName"))
        storyService.readNotes(projectName, traceName, traceFileName, edit = false)
            .onSuccess { notes ->
                ctx.json(notes)
                ctx.status(HttpServletResponse.SC_OK)
            }.onFailure {
                ctx.status(HttpServletResponse.SC_NOT_FOUND)
            }
    }


}