package it.unibz.krdb.kprime.view.controller

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.cs.semint.kprime.expert.adapter.ExpertPayload
import it.unibz.krdb.kprime.adapter.fact.FactDescriptor
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.expert.ExpertService
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.search.SearchResultType
import it.unibz.krdb.kprime.domain.search.SearchService
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse

class TermController(
    val termService: TermService,
    val prjContextService: PrjContextService,
    val userService: UserService,
    val dataService: DataService,
    val expertService: ExpertService,
    val searchService: SearchService,
) {

    var getProjectTermsMarkdown = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val prjContextLocation = prjContextService.projectByName(projectName)?.location?: PrjContext.NO_WORKING_DIR
        termService.markdown(prjContextLocation).fold(
            onSuccess =  { ctx.status(HttpServletResponse.SC_ACCEPTED) },
            onFailure = {
                ctx.result(it.localizedMessage)
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
            }
        )
    }

    val getTermByGid = Handler { ctx ->
        val gid = ctx.pathParam("gid")
        val term = termService.getTermByGid(TraceName(""),"","",gid)
        if (term!=null) {
            ctx.json(term)
            ctx.status(HttpServletResponse.SC_FOUND)
        } else {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        }
    }

    val getProjectFileTermPage = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val term = ctx.pathParam("term")
        val prjContext : PrjContext = prjContextService.projectByName(projectName)?: PrjContext.NO_PrjContext
        val fileContent = getTermPageContent(traceName,traceFileName,term, prjContext)
        ctx.html(fileContent)
    }

    val getProjectTermPage = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val term = ctx.pathParam("term")
        val prjContext : PrjContext = prjContextService.projectByName(projectName)?: PrjContext.NO_PrjContext
        if (prjContext.activeTermBase.isNotEmpty()) {
            val traceName = prjContext.activeTrace
            val traceFileName = prjContext.activeTermBase
            val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
            if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
            else {
                val fileContent = getTermPageContent(TraceName(traceName), traceFileName, term, prjContext)
                ctx.html(fileContent)
            }
        } else {
            ctx.redirect("/error/not_found_page.html",404)
        }
    }

    val getTermsPage = Handler { ctx ->
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val fileContent = getTermsPageContent(traceName,traceFileName, PrjContext.NO_PrjContext)
        ctx.html(fileContent)
    }

    val getProjectTermsPage = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val prjContext : PrjContext = prjContextService.projectByName(projectName)?: PrjContext.NO_PrjContext
        val traceName = prjContext.activeTrace
        val traceFileName = prjContext.activeTermBase
        val fileContent = getTermsPageContent(TraceName(traceName), traceFileName, prjContext)
        ctx.html(fileContent)
    }

    val getProjectTermsInfo = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = kotlin.runCatching { ctx.pathParam("traceName") }.getOrDefault("root")
        val traceFileName = kotlin.runCatching { ctx.pathParam("traceFileName") }.getOrDefault("base")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
//            val traceName = "root"
//            val traceFileName = "base"
            val termsInfo = dataService.getDatabaseInfo(PrjContextLocation(project.location), traceName, traceFileName)
            ctx.json(termsInfo)
            ctx.status(HttpServletResponse.SC_OK)
        }
    }

    val getProjectTerms = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName")) //"root"
        val traceFileName = ctx.pathParam("traceFileName") // "base"
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            //val terms = termService.listTerms(project.location, traceName, traceFileName)
            val terms = termService.getAllTerms(traceName, traceFileName,project.location)
            ctx.json(terms)
            ctx.status(HttpServletResponse.SC_OK)
        }
    }

    val putProjectTerms = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName")) //"root"
        val traceFileName = ctx.pathParam("traceFileName") // "base"
        val projectLocation = prjContextService.projectByName(projectName)?.location
        if (projectLocation==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            val newTodos = ctx.bodyAsClass<Array<Term>>()
            termService.writeAllTerms(projectLocation, traceName, traceFileName, newTodos.asList())
            ctx.status(HttpServletResponse.SC_ACCEPTED)
        }
    }

    val getTermsTrace =  Handler { ctx->
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        ctx.json(termService.getAllTerms(traceName,traceFileName, PrjContext.NO_PrjContext.location))
    }

    var getTerms = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val projectLocation = prjContextService.projectByName(projectName)?.location?:""
        val terms = termService.getAllTerms(traceName,traceFileName,projectLocation)
        ctx.json(terms)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var getTermsJsonApi = Handler { ctx : Context ->
        val projectName = PrjContextName(ctx.pathParam("projectName"))
        println("getTermsJsonApi.projectName:[$projectName]")
        val traceName = TraceName("root")//TraceName(ctx.pathParam("traceName"))
        val traceFileName = "base"//ctx.pathParam("traceFileName")
        kotlin.runCatching {
            termService.jsonApi(projectName, traceName, traceFileName )
        }.fold(
            onSuccess = {
                ctx.contentType("application/json")
                ctx.result(it.getOrElse { "{}" })
                ctx.status(HttpServletResponse.SC_ACCEPTED)
            },
            onFailure =  {
                ctx.result(it.localizedMessage)
                ctx.status(HttpServletResponse.SC_BAD_REQUEST)
            }
        )
    }

    var getTurtleTerms = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        val iriContext = ""
        val projectLocation = prjContextService.projectByName(projectName)?.location?:""
        val terms = termService.getAllTerms(traceName,traceFileName,projectLocation)
        val vocabularies = termService.readContextVocabularies(PrjContextLocation(projectLocation))
        val out = termService.toTurtle(projectName, vocabularies, terms, iriContext, PrjContextLocation(projectLocation))
        ctx.contentType("text/turtle")
        ctx.result(out)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }


    var putTerms = Handler { ctx : Context ->
        val newTerms = ctx.bodyAsClass<Array<Term>>()
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        termService.writeAllTerms(PrjContext.NO_WORKING_DIR,traceName,traceFileName,newTerms.asList())
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var putExpertTerms = Handler { ctx : Context ->
        val payload = ctx.bodyAsClass<ExpertPayload>()
        val newTerms = payload.result
        val traceName = TraceName(ctx.pathParam("traceName"))
        val traceFileName = ctx.pathParam("traceFileName")
        @Suppress("UNCHECKED_CAST")
        termService.writeAllTerms(PrjContext.NO_WORKING_DIR,traceName,traceFileName, newTerms as List<Term>)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getExperts =  Handler { ctx->
        ctx.json(expertService.listExperts())
    }

    val getVocabularies =  Handler { ctx->
        ctx.json(termService.readInstanceVocabularies())
    }

    val putVocabularies = Handler { ctx ->
        val newVocabularies = ctx.bodyAsClass<Array<Vocabulary>>()
        termService.writeInstanceVocabularies(newVocabularies.asList())
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getProjectVocabularies =  Handler { ctx->
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            ctx.json(termService.readContextVocabularies(PrjContextLocation(project.location)))
        }
    }

    var putProjectVocabularies = Handler { ctx : Context ->
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            val newVocabularies = ctx.bodyAsClass<Array<Vocabulary>>()
            termService.writeContextVocabularies(PrjContextLocation(project.location), newVocabularies.asList())
            ctx.status(HttpServletResponse.SC_ACCEPTED)
        }
    }


    private fun getTermPageContent(traceName: TraceName, traceFileName: String, termGid: String, prjContext: PrjContext): String {
        val prjContextLocation = if (prjContext.isNoProject()) PrjContext.NO_WORKING_DIR else prjContext.location
        val termType = "term"

        val allTerms = termService.getAllTerms(traceName, traceFileName,prjContext.location)
        val term = allTerms.firstOrNull { term-> term.gid == termGid}
            ?: allTerms.firstOrNull { term-> term.name == termGid}
            ?: Term("", "", "", "", "", "", "")
        val termName = term.name
        val findTermResult = searchService.findTerm(termName)
        val termStoriesUrls = findTermResult.filter { it.type == SearchResultType.STORY }.map { it.position }
        val termGoalsUrls = findTermResult.filter { it.type == SearchResultType.GOAL }.map { it.position }


        val templModel :MutableMap<String,Any> = mutableMapOf("title" to termType)
        templModel["traceName"] = traceName.value
        templModel["traceFileName"] = traceFileName
        templModel["projectName"] = prjContext.name
        templModel["termStories"] = termStoriesUrls
        templModel["termGoals"] = termGoalsUrls

        val database = dataService.getDatabase(PrjContextLocation(prjContextLocation),traceName.value,traceFileName).getOrDefault(Database().withGid())
        val table = database.schema.table(termName)?: Table()
        val hasAttributes = table.columns.isNotEmpty()
        val queryString = dataService.computeSelectString(table,database,null)
        val hasSources = database.source.isNotEmpty()

        val colTerms = allTerms.filter { aterm -> aterm.name.startsWith("$termName.") }
                .map { aterm ->
                    val colName =  aterm.name.substring(aterm.name.indexOf(".")+1)
                    val adbtype = table.colByName(colName)?.dbtype?:"-"
                    val facttype = table.colByName(colName)?.type?:"-"
                    val facturl = facttype.replace("schema:","https://schema.org/")
                    Term(aterm.name,aterm.category,aterm.relation,
                            adbtype,
                            facturl,aterm.description,aterm.labels) }

        val termConstraintDescription = FactDescriptor().describeConstraints(database,termName)
        val termLabelsDescription = describeLabels(term.labels)

        templModel["term"] = term
        templModel["termLabels"] = termLabelsDescription
        templModel["hasSample"] = hasSources && hasAttributes
        templModel["projectName"] = prjContext.name
        templModel["colTerms"] = colTerms
        templModel["queryString"] = queryString
        templModel["facts"] = termConstraintDescription

        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        templConfig.templateLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        val templ : Template = templConfig.getTemplate("/public/dictionary-$termType.html")
        val outWriter = StringWriter()
        templ.process(templModel, outWriter)
        return outWriter.buffer.toString()
    }

    private fun describeLabels(labels: String): String {
        return labels.split(",").joinToString("<br>")
    }

    private fun getTermsPageContent(traceName: TraceName, traceFileName: String, prjContext: PrjContext): String {
        val prjContextLocation = if (prjContext.isNoProject()) PrjContext.NO_WORKING_DIR else prjContext.location
        val templModel :MutableMap<String,Any> = mutableMapOf()
        templModel["projectName"] = prjContext.name
        templModel["traceName"] = traceName
        templModel["traceFileName"] = traceFileName
        val termsFilteredSorted = termService.getAllTerms(traceName, traceFileName, prjContextLocation)
        templModel["terms"] = termsFilteredSorted

        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        templConfig.templateLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        val templ : Template = templConfig.getTemplate("/public/project-dictionary.html")
        val outWriter = StringWriter()
        templ.process(templModel, outWriter)
        return outWriter.buffer.toString()
    }


}