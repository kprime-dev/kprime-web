package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.TermServices
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserService
import javax.servlet.http.HttpServletResponse

interface ChartCircleHandlers {
    val getCircleMapProjects: Handler
    val getCircleMapProjectsJson: Handler
}

class ChartCircleController(
    val userService: UserService,
    val prjContextService: PrjContextService,
    val todoService: TodoService,
    val termServices: TermServices,
    val traceServices: TraceService
) : ChartCircleHandlers {

    sealed class CircleTree {
        data class CircleNode(val name: String, val children: MutableList<CircleTree>) : CircleTree()
        data class CircleLeaf(val name: String, val value: Int, val type: String = "") : CircleTree()
    }

    override val getCircleMapProjects =  Handler { ctx ->
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        ctx.redirect("/circlemap/index.html")
    }


    override val getCircleMapProjectsJson =  Handler { ctx ->
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)

        val circleDiag = CircleTree.CircleNode("circles", mutableListOf())
        for (project in prjContextService.readAllProjects()) {

            val goals = todoService.all(projectLocation = project.location)
                    .map { shortLabel(it.title) }
            val circleGoals = CircleTree.CircleNode("goals", mutableListOf())
            for (goal in goals) {
                circleGoals.children.add(CircleTree.CircleLeaf(goal, 150, "goal"))
            }

            //println("Project  ${project.name},${project.activeTrace},${project.activeTermBase}")
            val terms = if (project.isActive())
                termServices.listAllTerms(project.location, TraceName(project.activeTrace),project.activeTermBase)
                        .map{ shortLabel(it.name) }
            else emptyList()
            val circleTerms = CircleTree.CircleNode("terms", mutableListOf())
            for (term in terms) {
                circleTerms.children.add(CircleTree.CircleLeaf(term, 100, "term"))
            }


            val stories = if (project.isActive())
                traceServices.getStoryNames(""+project.activeTrace, project.location)
            else emptyList()
            //traceService
            val circleStories = CircleTree.CircleNode("stories", mutableListOf())
            for (story in stories) {
                circleStories.children.add(CircleTree.CircleLeaf(story, 100, "story"))
            }

            circleDiag.children.add(
                    CircleTree.CircleNode(project.name, mutableListOf(
                            circleTerms,
                            circleStories,
                            circleGoals
                    ))
            )
        }
        ctx.json(circleDiag)
    }

    fun shortLabel(name: String): String {
        return if (name.length > 10) name.substring(0, 10) + "..." else name
    }
}