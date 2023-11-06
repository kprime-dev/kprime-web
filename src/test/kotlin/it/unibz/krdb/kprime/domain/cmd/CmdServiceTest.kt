package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.adapter.DataServiceAdapter
import it.unibz.krdb.kprime.adapter.RdfServiceAdapter
import it.unibz.krdb.kprime.adapter.jackson.JsonApiServiceAdapter
import it.unibz.krdb.kprime.adapter.jackson.file.*
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.expert.ExpertService
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.search.SearchService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.story.StoryService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.transformer.TransformerService
import it.unibz.krdb.kprime.domain.user.UserService
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class CmdServiceTest {

    companion object {
        fun getCmdService(): CmdService {
            val settingService = SettingService(SettingFileRepository("")) // Required real filesystem
            val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
            val actorService = ActorService(prjContextService, ActorFileRepository())
            val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(),DriverFileRepository())
            val rdfService = RdfServiceAdapter(settingService)
            val dataService = DataServiceAdapter(settingService, sourceService, rdfService) as DataService
            val expertService = ExpertService(settingService,ExpertFileRepository())
            val jsonApiService = JsonApiServiceAdapter()
            val termService = TermService(settingService, TermFileRepository(), dataService, rdfService, prjContextService, jsonApiService)
            val transformerService = TransformerService(settingService)
            val todoService = TodoService(settingService, TodoFileRepositoryBuilder())
            val traceService = TraceService(settingService,prjContextService)
            val storyService = StoryService(settingService, prjContextService, traceService)
            val searchService = SearchService(settingService, prjContextService, todoService, termService, storyService)
            val userService = UserService(settingService, UserFileRepository())
            val statService = StatService(sourceService,termService,todoService,traceService,dataService)
            return CmdService(
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
                statService
            )
        }
    }

    @Test
    @Ignore
    fun test_currentCmdContext_1() {
        // given
        val cmdService = getCmdService()
        // when
        val currentCmdContext1 = cmdService.currentCmdContext("actor1", "context1", "trashyy", emptyList())
        // then
        assertEquals("xxx",currentCmdContext1.env.database.id)

        // when context switch
        val currentCmdContext3 = cmdService.currentCmdContext("actor1", "context1", "trash2", emptyList())
        // then
        assertEquals("xyz",currentCmdContext3.env.database.id)

        // when new parallel context session
        val currentCmdContext2 = cmdService.currentCmdContext("actor1", "context2", "trash2", emptyList())
        // then
        assertEquals("xyz",currentCmdContext2.env.database.id)
    }
}