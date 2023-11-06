package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.actor.ActorService
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
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

data class CmdEnvironment(
    var author: String,
    var datasource: DataSource?,
    var database: Database,
    var changeSet: ChangeSet,
    val prjContextLocation: PrjContextLocation,
    val prjContextName: PrjContextName,
    val prjContextIRI: PrjContextIRI,
    var currentTrace: TraceName,
    var currentTraceFileName: String?,
    var currentParams: HashMap<String,Any>,
    var currentElement: String? = ""
)

data class CmdServicePool (
    val settingService: SettingService,
    val sourceService: SourceService,
    val expertService: ExpertService,
    val transformerService: TransformerService,
    val termService: TermService,
    val todoService: TodoService,
    val prjContextService: PrjContextService,
    val searchService: SearchService,
    val actorService: ActorService,
    val dataService: DataService,
    val storyService: StoryService,
    val rdfService: RdfService,
    val traceService: TraceService,
    val userService: UserService,
    val jsonService: JsonService,
    val statService: StatService
)

data class CmdContext(
    var env: CmdEnvironment,
    val pool: CmdServicePool,
    val envelope: CmdEnvelope
) {

    fun getCurrentTraceDir():String {
        val currentTraceDir = env.prjContextLocation.value+"/.kprime/traces/"+env.currentTrace?: ""
        println("CmdServicePool.getCurrentTraceDir : $currentTraceDir")
        return currentTraceDir  //pool.settingService.getTracesDir() + env.currentTrace + "/"
    }

    fun getCurrentTraceFilePath():String {
        return getCurrentTraceDir() + env.currentTraceFileName
    }

}
