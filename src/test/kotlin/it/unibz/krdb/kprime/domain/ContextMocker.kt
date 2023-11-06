package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.adapter.DataServiceAdapter
import it.unibz.krdb.kprime.adapter.RdfServiceAdapter
import it.unibz.krdb.kprime.adapter.jackson.JacksonService
import it.unibz.krdb.kprime.adapter.jackson.JsonApiServiceAdapter
import it.unibz.krdb.kprime.adapter.jackson.file.*
import it.unibz.krdb.kprime.adapter.jackson.file.DriverFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.ExpertFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.PrjContextFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.SourceFileRepository
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.CmdEnvelope
import it.unibz.krdb.kprime.domain.cmd.CmdEnvironment
import it.unibz.krdb.kprime.domain.cmd.CmdServicePool
import it.unibz.krdb.kprime.domain.expert.ExpertService
import it.unibz.krdb.kprime.domain.project.PrjContextIRI
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
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
import it.unibz.krdb.kprime.mock.*
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import kotlin.test.assertEquals

object ContextMocker {

    fun withEmptyDatabase(): CmdContext {
        val settingRepo = SettingRepositoryMock()
        val settingService = SettingService(settingRepo)
        val termRepo = TermRepositoryMock()
        val database = Database()
        assertEquals(0, database.schema.keys().size)
        val env = CmdEnvironment(
                "no-author",
                DataSource("", "", "", "", "", ""),
                database,
                ChangeSet(),
                PrjContextLocation.NO_PROJECT_LOCATION,
                PrjContextName.NO_PROJECT_NAME,
                prjContextIRI = PrjContextIRI(""),
                TraceName.NO_TRACE_NAME,
                "",
                HashMap(),
        )
        val dataService = DataServiceMock()
        val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
        val todoService = TodoService(settingService, TodoRepositoryBuilderMock())
        val rdfService = RdfServiceAdapter(settingService)
        val jsonApiService = JsonApiServiceAdapter()
        val termService = TermService(settingService, termRepo, dataService, rdfService, prjContextService, jsonApiService)
        val actorRepository = ActorRepositoryMock()
        val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(),DriverFileRepository())
        val traceService = TraceService(settingService,prjContextService)
        val storyService = StoryService(settingService,prjContextService, traceService)
        val userService = UserService(settingService, UserFileRepository())
        val statService = StatService(sourceService,termService,todoService,traceService,dataService)
        val pool = CmdServicePool(
                settingService,
                sourceService,
                ExpertService(settingService,ExpertFileRepository()),
                TransformerService(settingService),
                termService,
                todoService,
                prjContextService,
                SearchService(settingService,prjContextService,todoService,termService,storyService),
                actorService = ActorService(prjContextService, actorRepository),
                dataService = dataService,
                storyService,
                rdfService,
                traceService,
                userService,
                JacksonService(),
                statService
        )
        return CmdContext(env,pool, CmdEnvelope(emptyList(), "", emptyList()))
    }

    fun withTable(table: Table): CmdContext {
        val traceContext = withEmptyDatabase()
        traceContext.env.database.schema.tables().add(table)
        return traceContext
    }

    fun withH2MemDatabase(): CmdContext {
        val settingService = SettingService(SettingRepositoryMock())
        val database = Database()
        assertEquals(0, database.schema.keys().size)
        val datasource = DataSource("h2", "testdb", "org.h2.Driver", "jdbc:h2:mem:test_mem", "sa", "")
        datasource.connection = DataSourceConnection("test","sa","",true,true,false)
        val env = CmdEnvironment(
                "no-author",
                datasource,
                database,
                ChangeSet(),
                PrjContextLocation.NO_PROJECT_LOCATION,
                PrjContextName("mock"),
                prjContextIRI = PrjContextIRI(""),
                TraceName.NO_TRACE_NAME,
                "",
                HashMap()
        )
        val todoService = TodoService(settingService, TodoRepositoryBuilderMock())
        val prjContextService = PrjContextService(settingService, PrjContextRepositoryMock())
        val actorRepository = ActorRepositoryMock()
        val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(), DriverFileRepository())
        val rdfService = RdfServiceAdapter(settingService)
        val dataService = DataServiceAdapter(settingService,sourceService, rdfService)
        val jsonApiService = JsonApiServiceAdapter()
        val termService = TermService(settingService, TermRepositoryMock(), dataService, rdfService, prjContextService, jsonApiService)
        val traceService = TraceService(settingService,prjContextService)
        val storyService = StoryService(settingService,prjContextService, traceService)
        val userService = UserService(settingService, UserFileRepository())
        val statService = StatService(sourceService,termService,todoService,traceService,dataService)
        val expertService = ExpertService(settingService, ExpertFileRepository())
        val searchService = SearchService(settingService, prjContextService, todoService, termService, storyService)
        val transformerService = TransformerService(settingService)
        val actorService = ActorService(prjContextService, actorRepository)
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
        return CmdContext(env,pool,CmdEnvelope(emptyList(), "", emptyList()))
    }


}