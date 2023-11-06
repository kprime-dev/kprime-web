package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.chart.*
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserService
import javax.servlet.http.HttpServletResponse

interface ChartTraceHandlers {
    val getContextChartEntities: Handler // TODO switch to context
    val getContextChartEntityClasses: Handler // TODO switch to context

    val getProjectChartStoryActivities: Handler
    val getProjectActiveChartStoryActivities: Handler
    val getProjectChartStory: Handler
    val getProjectAsciidoc: Handler
    val getProjectChartLabelledClasses: Handler
    val getProjectSlide: Handler
    val getProjectChartClasses: Handler
    val getProjectChartLabels: Handler

    val getContextSlide: Handler
    val getContextChartStoryActivities: Handler
    val getContextChartEntity: Handler
    val getContextChartClasses: Handler
    val getContextChartFocus: Handler

    val getProjectChartLabelsFiltered: Handler
}

class ChartTraceController(
    val traceService: TraceService,
    termService: TermService,
    prjContextService: PrjContextService,
    userService: UserService,
    dataService: DataService,
    rdfService: RdfService) : ChartTraceHandlers {

    private val chartEntityService = ChartEntityService(traceService,dataService,prjContextService)
    private val chartEntitiesService = ChartEntitiesService(traceService)
    private val chartClassesService = ChartClassesService(traceService, dataService, prjContextService, rdfService)
    private val chartStoryService = ChartStoryService(traceService)
    private val chartFocusService = ChartFocusService(traceService,dataService,prjContextService)
    private val chartLabelsService = ChartLabelsService(prjContextService, rdfService)

    private val slideService = SlideService(traceService,termService)

    override val getContextChartFocus = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val fileContent = chartFocusService.chartFocusHtml(contextName,traceName.toDirName(), traceFileName, entityName)
        ctx.html(fileContent)
    }

    override val getContextChartStoryActivities = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val storyFileName = ctx.pathParam("storyFileName")
        val prjContext = prjContextService.projectByName(contextName)?: PrjContext.NO_PrjContext
        val fileContent = chartStoryService.getChartStoryActivitiesHtml(prjContext,traceName.toDirName(),storyFileName)
        ctx.html(fileContent)
    }

    override val getProjectChartStoryActivities = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val storyFileName = ctx.pathParam("storyFileName")
        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("","")
        val fileContent = chartStoryService.getChartStoryActivitiesHtml(prjContext,traceName.toDirName(),storyFileName)
        ctx.html(fileContent)
    }

    override val getProjectActiveChartStoryActivities = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val storyFileName = ctx.pathParam("storyFileName")
        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("","")
        val traceName = prjContext.activeTrace
        val fileContent = chartStoryService.getChartStoryActivitiesHtml(prjContext,traceName,storyFileName)
        ctx.html(fileContent)
    }

    override val getProjectChartStory = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val storyFileName = ctx.pathParam("storyFileName")
        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("","")
        val fileContent = chartStoryService.getChartStoryHtml(prjContext,traceName.toDirName(),storyFileName)
        ctx.html(fileContent)
    }

    override val getProjectAsciidoc = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val storyFileName = ctx.pathParam("storyFileName")
        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("","")
        val fileContent = chartStoryService.getAsciidocHtml(prjContext,traceName.toDirName(),storyFileName)
        ctx.html(fileContent)
    }

    override val getContextChartEntity = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val fileContent = chartEntityService.chartEntityHtml(traceName.toDirName(), traceFileName, entityName,contextName)
        ctx.html(fileContent)
    }

    override val getContextChartClasses =  Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val fileContent = chartClassesService.chartClassesHtml(traceName.toDirName(), traceFileName, contextName)
        ctx.html(fileContent)
    }

    override val getContextChartEntities =  Handler { ctx ->
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val fileContent = chartEntitiesService.chartEntitiesHtml(traceName.toDirName(), traceFileName)
        ctx.html(fileContent)
    }

    override val getProjectChartClasses =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val orient = ctx.queryParam("orient") ?: "TD"
        val prjContext : PrjContext = prjContextService.projectByName(projectName)?: PrjContext.NO_PrjContext
        if (prjContext.activeTermBase.isNotEmpty()) {
            val traceName = prjContext.activeTrace
            val traceFileName = prjContext.activeTermBase
            val fileContent = chartClassesService.chartColouredClassesHtml(traceName, traceFileName, projectName, orient)
            ctx.html(fileContent)
        } else {
            ctx.redirect("/error/not_found_page.html",404)
        }
    }

    override val getProjectChartLabels =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val fileContent = chartLabelsService.chartLabelsHtml(traceName = "", traceFileName = "",projectName)
        ctx.html(fileContent)
    }

    override val getProjectChartLabelsFiltered =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val labelFilter = ctx.pathParam("label")
        val fileContent = chartLabelsService.chartLabelsHtml(traceName = "", traceFileName = "",projectName,labelFilter)
        ctx.html(fileContent)
    }

    override val getProjectChartLabelledClasses =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val label = ctx.pathParam("label")
        val fileContent = chartClassesService.chartLabelledClassesHtml(projectName, traceName.toDirName(), traceFileName, label)
        ctx.html(fileContent)
    }

    override val getContextChartEntityClasses =  Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val fileContent = chartClassesService.chartEntitiesClassesHtml(traceName.toDirName(), traceFileName, contextName)
        ctx.html(fileContent)
    }

    override val getContextSlide =  Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val prjContext = prjContextService.projectByName(contextName)?: PrjContext.NO_PrjContext
        //val project = Project("", "")
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = slideService.getProjectSlideHtml(userApi, prjContext, traceName.toDirName(), traceFileName)
            ctx.html(fileContent)
        }
    }

    override val getProjectSlide =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("", "")
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val fileContent = slideService.getProjectSlideHtml(userApi, prjContext, traceName.toDirName(), traceFileName)
            ctx.html(fileContent)
        }
    }

}