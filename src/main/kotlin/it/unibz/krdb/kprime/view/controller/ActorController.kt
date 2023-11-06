package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.actor.Actor
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.cmd.CmdService
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddActor
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdListActors
import javax.servlet.http.HttpServletResponse

class ActorController(cmdService: CmdService, actorService: ActorService) {

    private data class ActorAPI(val role: String, val memberOf: String, val name: String)

    val replaceAllActors = Handler { ctx : Context ->
        //val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val actors = ctx.bodyAsClass<List<Actor>>()
        val contextName = ctx.pathParam("contextName")
        actorService.replaceAllActors(contextName, actors)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val getActors = Handler { ctx : Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val contextName = ctx.pathParam("contextName")
        val result = cmdService.parse(
            currentUser, "${TraceCmdListActors.getCmdName()} " +
                    "$contextName ", contextName = contextName
        )
        ctx.json(result)
    }

    var putActor = Handler { ctx : Context ->
        val actor = ctx.bodyAsClass<ActorAPI>()
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val contextName = ctx.pathParam("contextName")
        val result = cmdService.parse(
            currentUser, "${TraceCmdAddActor.getCmdName()} " +
                    "$contextName " +
                    "${actor.name} " +
                    "${actor.memberOf} " +
                    "${actor.role} ", contextName = contextName
        )
        ctx.json(result)
    }

}