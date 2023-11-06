package it.unibz.krdb.kprime.view.controller

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.cs.semint.kprime.expert.adapter.ExpertPayload
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.support.WebEncoder
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse

class TodoController(val todoService: TodoService, val prjContextService: PrjContextService) {

    var getProjectTodos = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val prjContextLocation = prjContextService.projectByName(projectName)?.location?: PrjContext.NO_WORKING_DIR
        val list = todoService.all(prjContextLocation)
        ctx.json(list)
    }

    var getProjectTodoMarkdown = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val prjContextLocation = prjContextService.projectByName(projectName)?.location?: PrjContext.NO_WORKING_DIR
        todoService.markdown(prjContextLocation).fold(
            onSuccess =  { ctx.status(HttpServletResponse.SC_ACCEPTED) },
            onFailure = {
                ctx.result(it.localizedMessage)
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
            }
        )
    }

    var putExpertProjectTodos = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val prjContextLocation = prjContextService.projectByName(projectName)?.location?: PrjContext.NO_WORKING_DIR
        val payload = ctx.bodyAsClass<ExpertPayload>()
        println(payload)
        val newTodos = payload.result as List<Todo>
        todoService.appendAll(prjContextLocation,newTodos)
        ctx.status(204)
    }

    var putProjectTodos = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val prjContextLocation = prjContextService.projectByName(projectName)?.location?: PrjContext.NO_WORKING_DIR
        val newTodos = ctx.bodyAsClass<Array<Todo>>()
        todoService.writeAll(prjContextLocation,newTodos.asList())
        ctx.status(204)
    }

    val getProjectTodoPage = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val todoGid = WebEncoder.decode(ctx.pathParam("todo"))
        val prjContext : PrjContext = prjContextService.projectByName(projectName)?: PrjContext.NO_PrjContext
        if (prjContext.activeTermBase.isNotEmpty()) {
            val traceName = prjContext.activeTrace
            val traceFileName = prjContext.activeTermBase
//            val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
            val todo = todoService.all(prjContext.location).firstOrNull { it.gid == todoGid }
            if (todo!=null) {
                val fileContent = getTodoPageContent(TraceName(traceName), traceFileName, todo, prjContext)
                ctx.html(fileContent)
            } else {
                ctx.redirect("/error/not_found_page.html",404)
            }
        } else {
            ctx.redirect("/error/not_found_page.html",404)
        }
    }

    private fun getTodoPageContent(
        traceName: TraceName,
        traceFileName: String,
        todo: Todo,
        prjContext: PrjContext
    ): String {

        val templModel :MutableMap<String,Any> = mutableMapOf("title" to "Todo")
        templModel["traceName"] = traceName.value
        templModel["todo"] = todo
        templModel["projectName"] = prjContext.name
        templModel["traceFileName"] = traceFileName

        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        templConfig.templateLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        val templ : Template = templConfig.getTemplate("/public/todo.html")
        val outWriter = StringWriter()
        templ.process(templModel, outWriter)
        return outWriter.buffer.toString()

    }


}