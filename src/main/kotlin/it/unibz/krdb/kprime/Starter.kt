package it.unibz.krdb.kprime

import io.javalin.Javalin
import io.javalin.core.util.RouteOverviewPlugin
import io.javalin.http.staticfiles.Location
import it.unibz.krdb.kprime.adapter.*
import it.unibz.krdb.kprime.adapter.jackson.JsonApiServiceAdapter
import it.unibz.krdb.kprime.adapter.jackson.file.*
import it.unibz.krdb.kprime.adapter.log4j.Log4JLoggerService
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.view.controller.TodoController
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.view.controller.TraceController
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.view.controller.TransformerController
import it.unibz.krdb.kprime.domain.transformer.TransformerService
import it.unibz.krdb.kprime.domain.cmd.CmdService
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.expert.ExpertService
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.search.SearchService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.view.Router
import it.unibz.krdb.kprime.view.ViewController
import it.unibz.krdb.kprime.view.ViewModel
import it.unibz.krdb.kprime.view.controller.*

const val version = "1.0.0"
val envKPPort: Int = (System.getenv("KPRIME_PORT") ?: "7000").toInt()

fun main(args: Array<String>) {

    println("kprime version [$version] webapp on port [$envKPPort] ")
    println("args [${args.joinToString(",")}]")
    if (args.isNotEmpty() && args[0] == "cli") {
        startCli()
    } else {
        startServer()
    }
}

fun startCli() {

    println("******************* START CLI")
    val settingService = SettingService(SettingFileRepository(""))
    val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
    val actorService = ActorService(prjContextService, ActorFileRepository())
    val userService = UserService(settingService, UserFileRepository())
    val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(), DriverFileRepository())
    val rdfService = RdfServiceAdapter(settingService)
    val dataService = DataServiceAdapter(settingService, sourceService, rdfService) as DataService
    val jsonApiService = JsonApiServiceAdapter()
    val expertService = ExpertService(settingService, ExpertFileRepository())
    val termService =
        TermService(settingService, TermFileRepository(), dataService, rdfService, prjContextService, jsonApiService)
    val transformerService = TransformerService(settingService)
    val todoService = TodoService(settingService, TodoFileRepositoryBuilder())
    val traceService = TraceService(settingService, prjContextService)
    val storyService = StoryService(settingService, prjContextService, traceService)
    val searchService = SearchService(settingService, prjContextService, todoService, termService, storyService)
    val statService = StatService(sourceService,termService,todoService,traceService,dataService)
    val cmdService = CmdService(
        settingService, sourceService, expertService, transformerService, termService,
        todoService, prjContextService, searchService, actorService, dataService, storyService,
        rdfService, traceService, userService, statService
    )
    val projectLocation = TraceName(System.getProperty("user.dir")).toDirName()

    print(">")
    var cmd = ""
    var contextName = ""
    val prjContext = prjContextService.projectByLocation(projectLocation)
    if (prjContext==null){
        println("location:[$projectLocation]")
        println("project context not found")
    } else {
        contextName = prjContext.name
    }
    println("******************* Ready to parse:")
    while (cmd!="quit") {
        cmd = readlnOrNull() ?: break
        val result = cmdService.parse("me", cmd, contextId = "", contextName = contextName)
        if (result.message.isNotEmpty()) println("result:"+result.message)
        if (result.warning.isNotEmpty()) println("warning:"+result.warning)
        if (result.failure.isNotEmpty()) println("failure:"+result.failure)
        print(">")
    }

}

fun startServer() {

    println("******************* START SERVER ")
    val javalin = Javalin.create { config ->
        config.addStaticFiles("/public", Location.CLASSPATH)
        config.registerPlugin(RouteOverviewPlugin("/routes"))
        config.showJavalinBanner = false
        config.accessManager{ handler, context, _ -> handler.handle(context) }
    }.start(envKPPort)
    val settingService = SettingService(SettingFileRepository(""))
    val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
    val actorService = ActorService(prjContextService, ActorFileRepository())
    val userService = UserService(settingService, UserFileRepository())
    val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(), DriverFileRepository())
    val rdfService = RdfServiceAdapter(settingService)
    val dataService = DataServiceAdapter(settingService, sourceService, rdfService) as DataService
    val jsonApiService = JsonApiServiceAdapter()
    val expertService = ExpertService(settingService, ExpertFileRepository())
    val termService =
        TermService(settingService, TermFileRepository(), dataService, rdfService, prjContextService, jsonApiService)
    val transformerService = TransformerService(settingService)
    val todoService = TodoService(settingService, TodoFileRepositoryBuilder())
    val traceService = TraceService(settingService, prjContextService)
    val storyService = StoryService(settingService, prjContextService, traceService)
    val searchService = SearchService(settingService, prjContextService, todoService, termService, storyService)
    val statService = StatService(sourceService,termService,todoService,traceService,dataService)
    val cmdService = CmdService(
        settingService, sourceService, expertService, transformerService, termService,
        todoService, prjContextService, searchService, actorService, dataService, storyService,
        rdfService, traceService, userService, statService
    )

    val loggerService = Log4JLoggerService()

    val viewModel = ViewModel(settingService)

    val router = Router(
        viewController = ViewController(settingService, viewModel),
        userController = UserController(userService),
        loginController = LoginController(userService, viewModel),
        settingsController = SettingsController(settingService, dataService, rdfService, prjContextService),
        todoController = TodoController(todoService, prjContextService),
        traceController = TraceController(cmdService, traceService, prjContextService, dataService),
        transformerController = TransformerController(transformerService),
        sourceController = SourceController(sourceService, settingService),
        dataController = DataController(settingService, prjContextService, dataService, loggerService, sourceService),
        termController = TermController(
            termService,
            prjContextService,
            userService,
            dataService,
            expertService,
            searchService,
        ),
        statController = StatController(statService),
        projectController = ProjectController(settingService,prjContextService, userService, rdfService),
        searchController = SearchController(searchService),
        actorController = ActorController(cmdService, actorService),
        chartController = ChartController(
            dataService,
            traceService,
            todoService,
            prjContextService,
            userService,
            termService,
            rdfService
        ),
        storyController = StoryController(cmdService, storyService)

    )
    router.initRoutes(javalin)
    CmdService.subscribe(router)
}
