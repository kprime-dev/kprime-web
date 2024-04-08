package it.unibz.krdb.kprime.view

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.Context
import it.unibz.krdb.kprime.adapter.pac4j.KPrimeSecurityConfigFactory
import it.unibz.krdb.kprime.adapter.pac4j.javalin.KPCallbackHandler
import it.unibz.krdb.kprime.adapter.pac4j.javalin.KPSecurityHandler
import it.unibz.krdb.kprime.view.controller.*

class Router(private val viewController: ViewController,
             private val userController: UserController,
             private val loginController: LoginController,
             private val settingsController: SettingsController,
             private val todoController: TodoController,
             private val dataController: DataController,
             private val traceController: TraceController,
             private val transformerController: TransformerController,
             private val sourceController: SourceController,
             private val termController: TermController,
             private val statController: StatController,
             private val projectController: ProjectController,
             private val searchController: SearchController,
             private val actorController: ActorController,
             private val chartController: ChartController,
             private val storyController: StoryController):EventListener {

    fun initRoutes(javalin: Javalin) {
        javalin.routes {
            ApiBuilder.get("/locale/{lang}") { ctx -> localeHandler(ctx) }
            //ApiBuilder.before(Filters.handleLocaleChange)
            //ApiBuilder.before(LogController.ensureLoggedBeforeViewingUsers)
            ApiBuilder.before(loginController.ensureLoginBeforeViewingUsers)
            val securityConfig = KPrimeSecurityConfigFactory().build()
            val kpCallbackHandler = KPCallbackHandler(securityConfig, defaultUrl="/",multiProfile = true)

            val kpGitHubHandler = KPSecurityHandler(securityConfig, "GitHubClient")
            val kpGitLabHandler = KPSecurityHandler(securityConfig, "OAuth20Client")

            // Private
            ApiBuilder.get(Path.Web.INDEX, viewController.serveIndexPage)
            ApiBuilder.get(Path.Web.LOGIN, loginController.serveLoginPage)
            ApiBuilder.post(Path.Web.LOGIN, loginController.handleLoginPost)
            ApiBuilder.post(Path.Web.LOGOUT, loginController.starterLocalLogoutHandler(securityConfig))
            ApiBuilder.get("/indexModel", settingsController.getIndexModel)
            ApiBuilder.get("/stats/vuca/{traceName}",statController.computeVuca)
            ApiBuilder.get("/callback", kpCallbackHandler)
            ApiBuilder.post("/callback", kpCallbackHandler)
            ApiBuilder.get("/github", kpGitHubHandler)
            ApiBuilder.get("/gitlab", kpGitLabHandler)
            ApiBuilder.get("/", viewController.serveIndexPage)
            ApiBuilder.get("/central-logout", loginController.starterCentralLogoutHandler(securityConfig))
            ApiBuilder.get("/gantt", chartController.getGanttChart)
            ApiBuilder.get("/barchart/{traceName}", chartController.getStackedChart)
            ApiBuilder.get("/barchart/entity/{traceName}", chartController.getStackedChartEntity)
            ApiBuilder.get("/terms/{traceName}/{traceFileName}", termController.getTermsTrace)
            ApiBuilder.put("/terms/{traceName}/{traceFileName}", termController.putTerms)
            ApiBuilder.get("/users", userController.getUsers)
            ApiBuilder.put("/users", userController.putUsers)
            ApiBuilder.put("/user",  userController.putUser)
            ApiBuilder.get("/settings", settingsController.getSettings)
            ApiBuilder.get("/cid/{cid}/settings", settingsController.getCidSettings)
            ApiBuilder.put("/settings", settingsController.putSettings)
            ApiBuilder.get("/projects", projectController.getProjects)
            ApiBuilder.put("/projects", projectController.putProjects)
            ApiBuilder.get("/notebook", traceController.notebook)
            ApiBuilder.get("/traces", traceController.getTraces)
            ApiBuilder.get("/trace/{traceName}", traceController.getTraceFiles)
            ApiBuilder.get("/tracesource/{sourceName}", traceController.getTraceSource)
            ApiBuilder.get("/tracegoal/{goalName}", traceController.getTraceGoal)
            ApiBuilder.get("/tracedb/{traceName}",traceController.getTraceDatabase)
            ApiBuilder.get("/tracetransf/{traceName}",traceController.getTraceTransfApplicability)
            ApiBuilder.get("/traceapply/{traceName}/{transformerName}",traceController.getTraceTransApply)
            ApiBuilder.get("/tracestree",traceController.getTraceTree)
            ApiBuilder.get("/vocabularies",termController.getVocabularies)
            ApiBuilder.put("/vocabularies",termController.putVocabularies)
            ApiBuilder.get("/experts",termController.getExperts)
            ApiBuilder.get("/sources",sourceController.getSources)
            ApiBuilder.put("/sources",sourceController.putSources)
            ApiBuilder.delete("/sourcedelete/{sourceName}",sourceController.deleteSource)
            ApiBuilder.post("/upload/{sourceName}", dataController.uploadCsv)
            ApiBuilder.post("/uploadSql/{sourceName}", dataController.uploadSql)
            ApiBuilder.get("/meta/{sourceName}",dataController.meta)
            ApiBuilder.get("/templates",storyController.getTemplates)
            ApiBuilder.get("/drivers",sourceController.getDrivers)
            ApiBuilder.get("/transfnames",transformerController.getTransformerNames)
            ApiBuilder.get("/transformer/{transformerName}",transformerController.getTransformer)
            ApiBuilder.put("/transformer",transformerController.putTransformer)
            ApiBuilder.put("/transformername",transformerController.renameTransformer)
            ApiBuilder.delete("/transfdelete/{transformerName}",transformerController.deleteTransformer)
            ApiBuilder.get("/dictionary/{traceName}/{traceFileName}", termController.getTermsPage)


            // Protected
            ApiBuilder.get("/context/{contextName}/noteedit/{traceName}/{fileName}", viewController.getNoteEditPage)
            ApiBuilder.get("/context/{contextName}/swagger", viewController.getContextSwaggerPage)
            ApiBuilder.delete("/context/{contextName}/trace/{traceName}", traceController.deleteTraceFiles)
            ApiBuilder.delete("/context/{contextName}/tracefile/{traceName}/{traceFileName}", traceController.deleteTraceFileContent)
            ApiBuilder.get("/context/{contextName}/template/{templateName}/{storyName}/{traceName}",storyController.putTemplate)
            ApiBuilder.put("/context/{contextName}/tracecommand",traceController.playCommands)
            ApiBuilder.get("/context/{contextName}/traceplay/{traceName}/{traceFileName}",traceController.playTraceFile)
            ApiBuilder.get("/context/{contextName}/chartactivities/{traceName}/{storyFileName}", chartController.getContextChartStoryActivities)
            ApiBuilder.get("/context/{contextName}/slide/{traceName}/{traceFileName}", chartController.getContextSlide)
            ApiBuilder.get("/context/{contextName}/actors", actorController.getActors)
            ApiBuilder.put("/context/{contextName}/actors", actorController.replaceAllActors)
            ApiBuilder.put("/context/{contextName}/actor",  actorController.putActor)
            ApiBuilder.get("/context/{contextName}/chart/classes/{traceName}/{traceFileName}", chartController.getContextChartClasses)
            ApiBuilder.get("/context/{contextName}/chart/focus/{traceName}/{traceFileName}/{entity}", chartController.getContextChartFocus)
            ApiBuilder.get("/context/{contextName}/chart/{traceName}/{traceFileName}/{entity}", chartController.getContextChartEntity)
            ApiBuilder.get("/context/{contextName}/chart/entities/{traceName}/{traceFileName}", chartController.getContextChartEntityClasses)
            ApiBuilder.get("/context/{contextName}/chart/{traceName}/{traceFileName}", chartController.getContextChartEntities)
            ApiBuilder.get("/context/{contextName}/sources",sourceController.getContextSources)
            ApiBuilder.put("/context/{contextName}/sources",sourceController.putContextSources)
            ApiBuilder.put("/context/{contextName}/source",sourceController.addContextSource)
            ApiBuilder.get("/context/{projectName}/todo", todoController.getProjectTodos)
            ApiBuilder.put("/context/{projectName}/todo", todoController.putProjectTodos)
            ApiBuilder.put("/context/{projectName}/todo/markdown", todoController.getProjectTodoMarkdown)
            ApiBuilder.get("/context/{projectName}/termsInfo/{traceName}/{traceFileName}", termController.getProjectTermsInfo)
            ApiBuilder.get("/context/{projectName}/terms/{traceName}/{traceFileName}", termController.getProjectTerms)
            ApiBuilder.put("/context/{projectName}/terms/{traceName}/{traceFileName}", termController.putProjectTerms)
            ApiBuilder.get("/context/{projectName}/traces", traceController.getProjectTraces)
            ApiBuilder.get("/context/{projectName}/traces/{traceName}", traceController.getProjectSubTraces)
            ApiBuilder.get("/context/{projectName}/files/{traceName}", traceController.getProjectStories)
            ApiBuilder.get("/context/{projectName}/filerename/{traceName}/{oldName}/{newName}", storyController.getProjectStoryRename)
            ApiBuilder.get("/context/{projectName}/tracebook/{traceName}/{traceFileName}", storyController.editTraceNotebook)
            ApiBuilder.put("/context/{projectName}/tracebook/{traceName}/{traceFileName}", storyController.writeTraceNotebook)
            ApiBuilder.put("/context/{projectName}/tracesave/{traceName}/{traceFileName}",traceController.putTraceFile)
            ApiBuilder.get("/context/{projectName}/storyedit/{traceFileName}", storyController.getProjectStoryEditPage)
            ApiBuilder.get("/context/{projectName}/storygoaledit/{goalID}/{traceFileName}", storyController.getProjectStoryEditPage)
            ApiBuilder.get("/context/{projectName}/vocabularies", termController.getProjectVocabularies)
            ApiBuilder.put("/context/{projectName}/vocabularies", termController.putProjectVocabularies)
            ApiBuilder.get("/context/{projectName}/dictionary/{traceName}/{traceFileName}/turtle", termController.getTurtleTerms)
            ApiBuilder.get("/context/{projectName}/dictionary/{traceName}/{traceFileName}/json", termController.getTerms)
            ApiBuilder.get("/context/{projectName}/dictionary/{traceName}/{traceFileName}/jsonapi", termController.getTermsJsonApi)
            ApiBuilder.put("/context/{projectName}/dictionary/markdown", termController.getProjectTermsMarkdown)
            ApiBuilder.get("/context/{projectName}/table/{traceName}/{traceFileName}/{entity}", dataController.getTablePage)

            // Expert
            ApiBuilder.get("/expert/{projectName}/todo", todoController.getProjectTodos)
            ApiBuilder.put("/expert/{projectName}/todo", todoController.putExpertProjectTodos)
            ApiBuilder.get("/expert/{projectName}/dictionary/{traceName}/{traceFileName}", termController.getTerms)
            ApiBuilder.put("/expert/{projectName}/dictionary/{traceName}/{traceFileName}", termController.putExpertTerms)
            ApiBuilder.get("/expert/{projectName}/document/{traceName}/{traceFileName}", storyController.getDocument)
            ApiBuilder.put("/expert/{projectName}/document/{traceName}/{traceFileName}", storyController.putDocument)
            ApiBuilder.get("/expert/{projectName}/database/{traceName}/{traceFileName}/json", dataController.getJsonDatabase)
            ApiBuilder.get("/expert/{projectName}/database/{traceName}/{traceFileName}/xml", dataController.getXmlDatabase)
            ApiBuilder.put("/expert/{projectName}/changeset/{traceName}/{traceFileName}", dataController.putExpertChangeset)
            ApiBuilder.put("/expert/{projectName}/database/{traceName}/{traceFileName}/json", dataController.putExpertJsonDatabase)
            ApiBuilder.put("/expert/{projectName}/database/{traceName}/{traceFileName}/xml", dataController.putExpertXmlDatabase)

            // CLI
            ApiBuilder.put( "/cli/{contextName}/tracecommand",traceController.playApiCommands)
            ApiBuilder.get( "/cli/{contextName}/suggestions/{command}",traceController.playApiSuggestions)
            ApiBuilder.get( "/cli/{contextName}/file/{fileName}",traceController.getFile)
            ApiBuilder.post("/cli/{contextName}/file/{fileName}",traceController.postFile)

            // Public read-only
            ApiBuilder.get("/search/{text}", searchController.getTermSearch)
            ApiBuilder.get("/myuser",userController.getUser)
            ApiBuilder.get("/provdata/{traceName}/{traceFileName}/{entity}", dataController.getLinkedProvenance)
            ApiBuilder.get("/events", traceController.getEventLog)
            ApiBuilder.get("/gid/term/{gid}", termController.getTermByGid)
            ApiBuilder.get("/goals/{projectName}/{traceName}/{traceFileName}", termController.getTerms)

            ApiBuilder.get("/project/{contextName}/noteview/{traceName}/{fileName}", viewController.getNoteViewPage)
            ApiBuilder.get("/project/", projectController.getProjectsPage)
            ApiBuilder.get("/project/hierarchy", chartController.getChartProjects)
            ApiBuilder.get("/project/circlemap", chartController.getCircleMapProjects)
            ApiBuilder.get("/project/circlemap/json", chartController.getCircleMapProjectsJson)
            ApiBuilder.get("/project/forcetree", chartController.getForceTreeProjects)
            ApiBuilder.get("/project/forcetree/json", chartController.getForceTreeProjectsJson)
            ApiBuilder.get("/project/{projectName}/forcetree/json", chartController.getForceTreeProjectJson)
            ApiBuilder.get("/project/{projectName}", projectController.getProjectPage)
            ApiBuilder.get("/project/{projectName}/publish/zip", projectController.publishZipProject)
            ApiBuilder.get("/project/{projectName}/publish/html", projectController.publishHtmlProject)
            ApiBuilder.get("/project/{projectName}/dictionary/{traceName}/{traceFileName}/{term}", termController.getProjectFileTermPage)
            ApiBuilder.get("/project/{projectName}/dictionary/{term}", termController.getProjectTermPage)
            ApiBuilder.get("/project/{projectName}/dictionary", termController.getProjectTermsPage)
            ApiBuilder.get("/project/{projectName}/todo/{todo}", todoController.getProjectTodoPage)
            ApiBuilder.get("/project/{projectName}/cases", chartController.getChartProjectCases)
            ApiBuilder.get("/project/{projectName}/hierarchy", chartController.getChartProjectHierarchy)
            ApiBuilder.get("/project/{projectName}/slide/{traceName}/{traceFileName}", chartController.getProjectSlide)
            ApiBuilder.get("/project/{projectName}/chartactivities/{traceName}/{storyFileName}", chartController.getProjectChartStoryActivities)
            ApiBuilder.get("/project/{projectName}/chartactivities/{storyFileName}", chartController.getProjectActiveChartStoryActivities)
            ApiBuilder.get("/project/{projectName}/doc/{traceName}/{storyFileName}", chartController.getProjectChartStory)
            ApiBuilder.get("/project/{projectName}/asciidoc/{traceName}/{storyFileName}", chartController.getProjectAsciidoc)
            ApiBuilder.get("/project/{projectName}/chart/classes", chartController.getProjectChartClasses)
            ApiBuilder.get("/project/{projectName}/chart/labels", chartController.getProjectChartLabels)
            ApiBuilder.get("/project/{projectName}/chart/labels/{label}", chartController.getProjectChartLabelsFiltered)
            ApiBuilder.get("/project/{projectName}/tracebook/{traceName}/{traceFileName}", storyController.readTraceNotebook)
            ApiBuilder.get("/project/{projectName}/ldata/{traceName}/{traceFileName}/{entity}", dataController.getProjectLinkedDataEntityList)
            ApiBuilder.get("/project/{projectName}/data/{traceName}/{traceFileName}/{entity}", dataController.getProjectDataEntityList)
            ApiBuilder.get("/project/{projectName}/story/{traceFileName}", storyController.getProjectStoryPage)
            ApiBuilder.get("/project/{projectName}/file/{traceName}/{traceFileName}", storyController.getProjectFile)
        }
    }

    private fun localeHandler(ctx: Context) {
        var lang = ctx.pathParam("lang")
        if ((lang != "en") && (lang != "zh")) lang = "en"
        ctx.sessionAttribute("locale", lang)
        ctx.res.sendRedirect("/")
    }

    override fun eventListen(docId: String, event: String) {

    }

    // WEB SOCKET NOTIFICATIONS


}