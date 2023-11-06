package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.log4j.Log4JLoggerService
import it.unibz.krdb.kprime.domain.user.User
//import org.apache.logging.log4j.LogManager

object LogController {

    val logger = Log4JLoggerService() // LogManager.getLogger(LogController.javaClass)

    val skipList = listOf("/img","/js/","/css")

    var ensureLoggedBeforeViewingUsers = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)
        if (ctx.path().length > 4 && !skipList.contains(ctx.path().substring(0,4))) {
            val loggedLine = " | $currentUser | ${ctx.method()} | ${ctx.path()} | ${ctx.queryString()} | | ${ctx.pathParamMap()}"
            logger.info(loggedLine)
        }
    }
}