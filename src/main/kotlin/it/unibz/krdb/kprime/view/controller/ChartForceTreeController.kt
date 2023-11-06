package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.TermServices
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.user.UserService
import javax.servlet.http.HttpServletResponse

interface ChartForceTreeHandlers {
    val getForceTreeProjects: Handler
    val getForceTreeProjectsJson: Handler
    val getForceTreeProjectJson: Handler
}

class ChartForceTreeController(
    val userService: UserService,
    val prjContextService: PrjContextService,
    val todoService: TodoService,
    val termServices: TermServices,
    val traceServices: TraceService
) : ChartForceTreeHandlers {
    enum class NodeType { Term , Story, Goal, Project }

    sealed class CircleTree {
        data class CircleNode(val name: String,
                              val children: MutableList<CircleTree>,
                              val type: String = "",
                              val detail_url: String = "") : CircleTree()
        data class CircleLeaf(val name: String,
                              val value: Int,
                              val type: String = "",
                              val detail_url: String = "",) : CircleTree()
    }

    override val getForceTreeProjects =  Handler { ctx ->
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        ctx.redirect("/forcetree/index.html")
    }


    override val getForceTreeProjectsJson =  Handler { ctx ->
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val circleDiag = CircleTree.CircleNode("circles", mutableListOf())
            for (project in prjContextService.readAllProjects()) {
                getProjectChildren(project, circleDiag)
            }
            ctx.json(circleDiag)
        }
    }

    override val getForceTreeProjectJson =  Handler { ctx ->
        val projectName = ctx.pathParam("projectName")
        val userApi = userService.userApiFromName(UserController.userName(ctx)?: User.NO_USER)
        if (userApi==null) ctx.status(HttpServletResponse.SC_NOT_FOUND)
        else {
            val project = prjContextService.projectByName(projectName)
            if (project == null) ctx.status(HttpServletResponse.SC_NOT_FOUND) else {
                val circleDiag = CircleTree.CircleNode("circles", mutableListOf())
                getProjectChildren(project, circleDiag)
                ctx.json(circleDiag)
            }
        }
    }


    private fun getProjectChildren(
        prjContext: PrjContext,
        circleDiag: CircleTree.CircleNode
    ) {
        val goals = if (prjContext.isActive())
                        todoService.all(projectLocation = prjContext.location)
                    else emptyList()
        val circleGoals = CircleTree.CircleNode("goals", mutableListOf())
        for (goal in goals) {
            val detailUrl = "/project/${prjContext.name}/todo/${goal.gid}"
            circleGoals.children.add(CircleTree.CircleLeaf(goal.title, 150, NodeType.Goal.name, detailUrl))
        }

        val terms = if (prjContext.isActive())
                        termServices.listAllTerms(prjContext.location, TraceName(prjContext.activeTrace), prjContext.activeTermBase)
                    else emptyList()
        val circleTerms = CircleTree.CircleNode("terms", mutableListOf())
        for (term in terms) {
            val detailUrl = "/project/${prjContext.name}/dictionary/${term.gid}"
            circleTerms.children.add(CircleTree.CircleLeaf(term.name, 100, NodeType.Term.name, detailUrl))
        }


        val stories = if (prjContext.isActive())
                        traceServices.getStoryNames("" + prjContext.activeTrace, prjContext.location)
                      else emptyList()
        val circleStories = CircleTree.CircleNode("stories", mutableListOf())
        for (story in stories) {
            val traceName = TraceName(prjContext.activeTrace).toUrlName()
            val detailUrl = "/noteview.html?pr=${prjContext.name}&tr=___&tf=$story.md"
            circleStories.children.add(CircleTree.CircleLeaf(story, 100, NodeType.Story.name, detailUrl))
        }

        val detailUrl = "/project/${prjContext.name}"
        circleDiag.children.add(
            CircleTree.CircleNode(
                prjContext.name, mutableListOf(
                    circleTerms,
                    circleStories,
                    circleGoals
                ), NodeType.Project.name, detailUrl
            )
        )
    }

    fun shortLabel(name: String): String {
        return if (name.length > 10) name.substring(0, 10) + "..." else name
    }
}