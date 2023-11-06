package it.unibz.krdb.kprime.view.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.core.util.FileUtil
import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.cs.semint.kprime.expert.adapter.ExpertPayload
import it.unibz.krdb.kprime.adapter.chart.ChartGoalsService
import it.unibz.krdb.kprime.adapter.log4j.Log4JLoggerService
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.trace.TraceName
//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import org.apache.logging.log4j.message.StringFormatterMessageFactory
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import java.net.URLDecoder
import javax.servlet.http.HttpServletResponse

class DataController(
    settingService: SettingService,
    prjContextService: PrjContextService,
    dataService: DataService,
    loggerService: LoggerService,
    sourceService: SourceService
) {

    //val logger: Logger = LogManager.getLogger(StringFormatterMessageFactory.INSTANCE)
    val logger = Log4JLoggerService()

    val uploadCsv = Handler { ctx ->
        val toUploadSourceName = ctx.pathParam("sourceName")
        ctx.uploadedFiles("files").forEach { (contentStream, contentType, dimension, filename) ->
            with(logger) {
                debug("content type: $contentType")
                debug("bytes: $dimension")
                debug("name: $filename")
            }
            val datasource = sourceService.newWorkingDataSource(toUploadSourceName)?: return@Handler
            val workingDataDir = settingService.getWorkingDir() + "data/"
            FileUtil.streamToFile(contentStream, workingDataDir + filename)
            val tablename = filename.split(".")[0]
            dataService.createTableFromFileCsv(workingDataDir, filename, datasource, tablename)
            ctx.res.sendRedirect("/sources.html")
        }
    }

    val uploadSql = Handler { ctx ->
        val toUploadSourceName = ctx.pathParam("sourceName")
        ctx.uploadedFiles("files").forEach { (contentStream, contentType, dimension, filename) ->
            with(logger) {
                debug("content type: $contentType")
                debug("bytes: $dimension")
                debug("name: $filename")
            }
            val workingDataDir = settingService.getWorkingDir() + "data/"
            val datasource = sourceService.newWorkingDataSource(toUploadSourceName)?: return@Handler
            FileUtil.streamToFile(contentStream, workingDataDir + filename)
            dataService.createTableFromFileSql(workingDataDir, filename, datasource)
            ctx.res.sendRedirect("/sources.html")
        }
    }

    val getProjectLinkedDataEntityList =  Handler {ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val conditions = URLDecoder.decode(ctx.queryString()?: "",Charsets.UTF_8.name())

        val project = prjContextService.projectByName(projectName)?: throw IllegalArgumentException("Project $projectName not found.")
        dataService.getDatabase(PrjContextLocation(project.location), traceName, traceFileName)
            .map { Pair(it, dataService.getTable(it,entityName)) }
            .mapCatching { (db,resultTable) ->
                val table = resultTable.getOrNull()?: throw IllegalArgumentException("Table $entityName not found.")
                val entity = dataService.queryLinkedEntity(project, table, db, conditions)
                ctx.header("Content-Type","application/json")
                ctx.result(entity)
                ctx.status(HttpServletResponse.SC_OK)
            }.onFailure {
                ctx.result(it.message?:"getProjectLinkedDataEntityList failure.")
                ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
            }
    }

    val getTablePage =  Handler {ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val conditions = URLDecoder.decode(ctx.queryString()?: "",Charsets.UTF_8.name())

        val project = prjContextService.projectByName(projectName)?: throw IllegalArgumentException("Project $projectName not found.")
        dataService.getDatabase(PrjContextLocation(project.location), traceName, traceFileName)
                .map { Pair(it, dataService.getTable(it,entityName)) }
                .mapCatching { (db,resultTable) ->
                    val table = resultTable.getOrNull()?: throw IllegalArgumentException("Table $entityName not found.")
                    val entity = dataService.queryJsonEntity(project, table, db, conditions)
                    val columns = table.columns.joinToString(",") { "{ title:\"${it.name}\",field:\"${it.name}\"}" }
                    ctx.header("Content-Type","application/json")
                    var fileContent = ChartGoalsService::class.java.getResource("/public/tabulator.html")!!.readText()
                    fileContent = fileContent.replace("{{tableData}}", entity)
                    fileContent = fileContent.replace("{{tableColumns}}", columns)
//                    fileContent = fileContent.replace("{{project.name}}", prjContext?.name?:"")
                    ctx.html(fileContent)
                    ctx.status(HttpServletResponse.SC_OK)
                }.onFailure {
                    ctx.result(it.message?:"getProjectLinkedDataEntityList failure.")
                    ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
                }
    }

    val getLinkedProvenance =  Handler {ctx ->
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val conditions = URLDecoder.decode(ctx.queryString()?: "",Charsets.UTF_8.name())

        dataService.getDatabase(PrjContextLocation.NO_PROJECT_LOCATION, traceName, traceFileName)
            .map { Pair(it, dataService.getTable(it,entityName)) }
            .map {
                val table = it.second.getOrNull()?: throw IllegalArgumentException("Table $entityName not found.")
                val entity = dataService.queryJsonProvenance(table, it.first, conditions)
                ctx.header("Content-Type","application/json")
                ctx.result(entity)
                ctx.status(HttpServletResponse.SC_OK)
            }
    }

    val getProjectDataEntityList = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        val entityName = ctx.pathParam("entity")
        val conditions = URLDecoder.decode(ctx.queryString()?: "",Charsets.UTF_8.name())

        loggerService.debug("dataService.getDatabase START")
        val project = prjContextService.projectByName(projectName)?: throw IllegalArgumentException("Project $projectName not found.")
        dataService.getDatabase(PrjContextLocation(project.location), traceName, traceFileName)
            .onFailure { println("dataService.getDatabase NOT FOUND") }
            .map { Pair(it, dataService.getTable(it,entityName)) }
            .onFailure { println("dataService.getDatabase TABLE NOT FOUND") }
            .mapCatching {
                loggerService.debug("dataService.getDatabase.TABLE:$entityName")
                val table = it.second.getOrNull()?: throw IllegalArgumentException("Table $entityName not found.")
                loggerService.debug("dataService.getDatabase.TABLE:$table")
                val database = it.first
                val entity = dataService.queryJsonEntity(project, table, database, conditions)
                ctx.header("Content-Type","application/json")
                ctx.result(entity)
                ctx.status(HttpServletResponse.SC_OK)
            }.onFailure {
                when(it){
                    is IllegalArgumentException -> {
                        loggerService.debug("is IllegalArgumentException [${it.message}]")
                    } else -> {
                        loggerService.debug(it.stackTraceToString())
                    }
                }
                ctx.result(it.message?:"getProjectDataEntityList failure." )
                ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
            }
    }

    val getJsonDatabase = Handler { ctx ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val projectLocation = prjContextService.projectByName(projectName.value)?.location?:""
        ctx.json(dataService.getDatabase(PrjContextLocation(projectLocation),traceName.value,traceFileName).getOrDefault(Database().withGid()))
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getXmlDatabase = Handler { ctx ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val projectLocation = prjContextService.projectByName(projectName.value)?.location?:""
        val database = dataService.getDatabase(PrjContextLocation(projectLocation),traceName.value,traceFileName).getOrDefault(Database().withGid())
        val traceFileContent = XMLSerializerJacksonAdapter().prettyDatabase(database)
        ctx.html(traceFileContent)
        ctx.contentType("text/xml")
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var putExpertChangeset = Handler { ctx : Context ->
        val payload = ctx.bodyAsClass<ExpertPayload>()
        println(payload)
        val newChangeset =  jacksonObjectMapper().readValue<ChangeSet>(payload.result as String)
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        dataService.addChangeSet(traceName,traceFileName, newChangeset)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var putExpertJsonDatabase = Handler { ctx : Context ->
        val payload = ctx.bodyAsClass<ExpertPayload>()
        println(payload)
        val newDatabase =  jacksonObjectMapper().readValue<Database>(payload.result as String)
        val traceName = ctx.pathParam("traceName")
        val traceFileName = ctx.pathParam("traceFileName")
        dataService.addDatabase(traceName,traceFileName, newDatabase)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var putExpertXmlDatabase = Handler { ctx : Context ->
        val payload = ctx.bodyAsClass<ExpertPayload>()
        println(payload)
        val newDatabase =  dataService.databaseFromXml(payload.result as String)
        val traceName = ctx.pathParam("traceName").replace("___","/")
        val traceFileName = ctx.pathParam("traceFileName")
        dataService.addDatabase(traceName,traceFileName, newDatabase)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    data class MetaAPI (val error:String, val content:String)
    val meta = Handler { ctx ->
        val toMetaSourceName = ctx.pathParam("sourceName")
        val metaDatabase = dataService.databaseFromSourceOrNewH2(toMetaSourceName)
        val content = dataService.prettyDatabase(metaDatabase)
        ctx.json (MetaAPI("", content))

    }

}