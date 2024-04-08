package it.unibz.krdb.kprime.view.controller

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.jackson.JacksonProject
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.LabelField
import it.unibz.krdb.kprime.support.FolderZipper
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.support.MdHtmlPublisher
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.StringWriter
import javax.servlet.http.HttpServletResponse

class ProjectController(prjContextService: PrjContextService, userService: UserService, rdfService: RdfService) {

    val getProjectsPage =  Handler { ctx: Context ->
        val templModel :MutableMap<String,Any> = mutableMapOf()
        val mode = ctx.queryParam("mode")
        templModel["projects"] = prjContextService.readAllProjects().filter { it.activeTermBase.isNotEmpty() }
        templModel["projectsInactive"] = prjContextService.readAllProjects().filter { it.activeTermBase.isEmpty() }
        if (mode=="grid")
            ctx.html(htmlFromTemplate("/public/projects-grid.html", templModel))
        else
            ctx.html(htmlFromTemplate("/public/projects-table.html", templModel))
    }

    private fun htmlFromTemplate(templatePage: String, templModel: MutableMap<String, Any>): String {
        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        templConfig.templateLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        val templ: Template = templConfig.getTemplate(templatePage)
        val outWriter = StringWriter()
        templ.process(templModel, outWriter)
        return outWriter.buffer.toString()
    }

    val getProjectPage =  Handler { ctx: Context ->
        val projectName = ctx.pathParam("projectName")
        val projects = prjContextService.readAllProjects()
        val templModel :MutableMap<String,Any> = mutableMapOf()
        val project = if (projectName.contains('-'))
            projects.firstOrNull { it.gid == projectName }
        else
            projects.firstOrNull { it.name == projectName }
        if (project==null) {
            ctx.status(HttpServletResponse.SC_NOT_FOUND)
        } else {
            val users = userService.getUsers(project.location)
            val iriContext = "http://kp.getProjectPage/"
            val rdfDataDir = RdfService.getPrjContextRdfDataDir(PrjContextLocation(project.location))
            val rdfStatements = rdfService.findStatements(iriContext,
                LabelField(projectName), LabelField("_"),"_",
                rdfDataDir)
                .getOrDefault(emptyList())
            val rdfHtmlLabels = rdfStatements.map { " ${it.predicate} ${it.cobject} " }

            if (project.activeTermBase.isNotEmpty()) {
                templModel["dictionaryUrl"] = "/project/$projectName/dictionary"
                templModel["hasDictionaryUrl"] = true
            } else {
                templModel["hasDictionaryUrl"] = false
            }
            templModel["projectHasPic"] = project.picUrl.isNotEmpty()
            templModel["project"] = project
            templModel["rdfLabels"] = rdfHtmlLabels
            templModel["users"] = users
            ctx.html(htmlFromTemplate("/public/project.html", templModel))
        }
    }

    var getProjects = Handler { ctx: Context ->
        val projects = prjContextService.readAllProjects()
        ctx.json(projects)
    }

    var putProjects = Handler { ctx ->
        val projects = ctx.bodyAsClass<Array<JacksonProject>>()
        println("projects: $projects")
        prjContextService.writeAllProjects(projects.map { JacksonProject.toProject(it) })
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    // https://mkyong.com/java/how-to-compress-files-in-zip-format/
    // https://www.codeproject.com/articles/30561/using-zip-content-for-delivery-over-http
    // https://javalin.io/documentation#context
    var publishZipProject = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(404)
        } else {
            val zipFile = FolderZipper().zip(project.location)
            ctx.result(zipFile.inputStream())
            ctx.contentType("application/zip")
            ctx.status(200)
        }
    }

    var publishHtmlProject = Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val project = prjContextService.projectByName(projectName)
        if (project==null) {
            ctx.status(404)
        } else {
            val publisher = MdHtmlPublisher()
            publisher.translateFolder(
                project.location,
                "/home/nipe/Temp/published-html",
                singleFile = false,
                createToc = true)
            val zipFile = FolderZipper().zip("/home/nipe/Temp/published-html")
            ctx.result(zipFile.inputStream())
            ctx.contentType("application/zip")
            ctx.status(200)
        }
    }

}