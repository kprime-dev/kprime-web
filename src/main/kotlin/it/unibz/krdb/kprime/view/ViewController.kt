package it.unibz.krdb.kprime.view

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.SwaggerService
import it.unibz.krdb.kprime.domain.setting.SettingService

class ViewController(val settingService: SettingService, val viewModel: ViewModel) {

    var serveIndexPage = Handler { ctx: Context ->
        ctx.redirect(Path.Page.INDEX)
//        @Suppress("UNCHECKED_CAST") val model: MutableMap<String, Any> = viewModel.baseModel(ctx) as MutableMap<String, Any>
//        model["loggedOut"] = removeSessionAttrLoggedOut(ctx)
//        model["loginRedirect"] = removeSessionAttrLoginRedirect(ctx) ?: ""
//        ctx.render(Path.Template.INDEX, model)
    }

    val getContextSwaggerPage = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val prjContext = "" //prjContextService.projectByName(contextName)?: PrjContext.NO_PrjContext
        val fileContent = SwaggerService().htmlSwagger(contextName)
        ctx.html(fileContent)
    }

    val getNoteEditPage = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = ctx.pathParam("traceName")
        val fileName = ctx.pathParam("fileName")
        ctx.redirect("/noteedit.html?pr=$contextName&tr=$traceName&tf=$fileName")
    }

    val getNoteViewPage = Handler { ctx ->
        val contextName = ctx.pathParam("contextName")
        val traceName = ctx.pathParam("traceName")
        val fileName = ctx.pathParam("fileName")
        ctx.redirect("/noteview.html?pr=$contextName&tr=$traceName&tf=$fileName")
    }

}