package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.Driver
import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.source.SourceService
import java.io.File
import javax.servlet.http.HttpServletResponse

class SourceController(val sourceService: SourceService, val settingService: SettingService) {

    // FIXME Create a logger service
   // val logger: Logger = LogManager.getLogger(StringFormatterMessageFactory.INSTANCE)

    val deleteSource = Handler { ctx ->
        val toDeleteSourceName = ctx.pathParam("sourceName")
        val sourceToDelete = sourceService.getInstanceSourceByName(toDeleteSourceName)?: return@Handler
        sourceService.dropInstanceSource(toDeleteSourceName)
        if (sourceToDelete.type == "xml") {
            File(sourceToDelete.location).delete()
        }
    }

    var getSources = Handler { ctx : Context ->
        //val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)
        //this.logger.error(" $currentUser GET SOURCES")
        ctx.json(sourceService.readInstanceSources())
    }

    var getContextSources = Handler { ctx : Context ->
        //val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)
        val contextName = ctx.pathParam("contextName")
        ctx.json(sourceService.readContextSources(contextName))
    }

    var putContextSources = Handler { ctx: Context ->
        val contextName = ctx.pathParam("contextName")
        val sources = ctx.bodyAsClass<Array<Source>>().toMutableList()
        sourceService.writeContextSources(contextName,sources)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var addContextSource = Handler { ctx: Context ->
        val contextName = ctx.pathParam("contextName")
        val source = ctx.bodyAsClass<Source>()
        sourceService.addContextSource(contextName,source)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var putSources = Handler { ctx: Context ->
        val sources = ctx.bodyAsClass<Array<Source>>().toMutableList()
        sourceService.writeInstanceSources(sources)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    var getDrivers = Handler { ctx : Context ->
        ctx.json(sourceService.readAllInstanceDriversPlusH2())
    }

    var putDrivers = Handler { ctx: Context ->
        val drivers = ctx.bodyAsClass<Array<Driver>>().toMutableList()
        sourceService.writeAllInstanceDrivers(drivers)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

}
