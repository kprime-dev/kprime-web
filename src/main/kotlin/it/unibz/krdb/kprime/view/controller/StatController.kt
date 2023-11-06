package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.StatService

class StatController(val statService: StatService) {
    val computeVuca = Handler { ctx ->
        val traceName = ctx.pathParam("traceName")
        ctx.json(statService.computeVUCA(traceName))
    }
}