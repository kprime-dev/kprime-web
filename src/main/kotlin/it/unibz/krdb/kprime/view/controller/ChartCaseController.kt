package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.chart.ChartGoalsService
import it.unibz.krdb.kprime.adapter.chart.ChartPrjContextsService
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.user.UserService
import javax.servlet.http.HttpServletResponse

interface ChartCaseHandlers {
    val getChartCases: Handler
    val getChartProjects: Handler
    val getChartProjectHierarchy: Handler
    val getChartProjectCases: Handler
}

class ChartCaseController(
    todoService: TodoService,
    prjContextService: PrjContextService,
    userService: UserService,
    rdfService: RdfService
) : ChartCaseHandlers {

    private val chartGoalsService = ChartGoalsService(todoService)
    private val chartPrjContextsService = ChartPrjContextsService(prjContextService)

    override val getChartCases =  Handler { ctx ->
        val prjContext = PrjContext("", PrjContext.NO_WORKING_DIR)
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = chartGoalsService.chartCase(prjContext,userApi.role)
            ctx.html(fileContent)
        }
    }

    override val getChartProjects =  Handler { ctx ->
        val prjContext = PrjContext("", PrjContext.NO_WORKING_DIR)
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = chartPrjContextsService.chartAllProjectsHierarchies(prjContext,userApi.role,rdfService)
            ctx.html(fileContent)
        }
    }

    override val getChartProjectHierarchy =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val prjContext: PrjContext = prjContextService.projectByName(projectName) ?: return@Handler
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = chartPrjContextsService.chartProjectHierarchy(prjContext,userApi.role)
            ctx.html(fileContent)
        }
    }

    override val getChartProjectCases =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val prjContext: PrjContext? = prjContextService.projectByName(projectName)
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = chartGoalsService.chartCase(prjContext,userApi.role)
            ctx.html(fileContent)
        }
    }

}