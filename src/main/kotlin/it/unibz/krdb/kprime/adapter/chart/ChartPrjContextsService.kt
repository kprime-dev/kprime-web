package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.adapter.RdfStatement
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.LabelField

class ChartPrjContextsService(val prjContextService: PrjContextService) {

    fun chartAllProjectsHierarchies(prjContext: PrjContext?, userRole: String, rdfService: RdfService): String {
        val projects = prjContextService.readAllProjects()
        val projectsRdfEnriched = rdfEnrich(projects,rdfService)
        val markdownDiag = prepareMarkDownDiag(projectsRdfEnriched)
        //val markdownDiag = prepareMarkDownDiag(projects)

        val menu = prepareMenu(userRole)
        var fileContent = ChartPrjContextsService::class.java.getResource("/public/chart-contexts.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        fileContent = fileContent.replace("{{menu}}", menu)
        fileContent = fileContent.replace("{{project.name}}", prjContext?.name?:"")
        return fileContent
    }

    private fun rdfEnrich(projects: List<PrjContext>, rdfService: RdfService): List<PrjContext> {
        return projects.map { prj -> prjRdfEnrich(prj, rdfService) }
    }

    private fun prjRdfEnrich(prjContext: PrjContext, rdfService: RdfService): PrjContext {
        val iriContext = "http://kp.prjRdfEnrich/"
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(PrjContextLocation(prjContext.location))
        val rdfLabels = rdfService.findStatements(iriContext,
            LabelField(prjContext.name), LabelField("_"), "_",
            rdfDataDir)
        val rdfStrings = rdfLabels.getOrDefault(emptyList()).map { rdfLabel -> toRdfString(rdfLabel) }
        if (rdfStrings.isNotEmpty()) prjContext.addLabels(rdfStrings)
        return prjContext
    }

    private fun toRdfString(rdfLabel: RdfStatement) =
        rdfLabel.subject.replace(":", "").replace("/", "_")+ " " +
        rdfLabel.predicate.replace(":", "").replace("/", "_")+ " " +
        rdfLabel.cobject.replace(":", "").replace("/", "_")


    fun chartProjectHierarchy(rootPrjContext: PrjContext, userRole:String): String {
        val projects = prjContextService.readAllProjects()
        val idsInHierarchy= identifyProjectIdsInHierarchy(projects, rootPrjContext.gid)
        //println(idsInHierarchy)
        val projectsInHierarchy= selectProjectsWithIds(projects, idsInHierarchy)
//        println(projectsInHierarchy.size)
//        println(projectsInHierarchy)
        val markdownDiag = prepareMarkDownDiag(projectsInHierarchy)
        val menu = prepareMenu(userRole)
        var fileContent = ChartPrjContextsService::class.java.getResource("/public/chart-contexts.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        fileContent = fileContent.replace("{{menu}}", menu)
        fileContent = fileContent.replace("{{project.name}}", rootPrjContext.name)
        return fileContent
    }

    // Identify projectIds in hierarchy with project
    private fun identifyProjectIdsInHierarchy(prjContexts: List<PrjContext>, rootProjectGid: String): Set<String> {
        val idsInHierarchy = mutableSetOf<String>()
        idsInHierarchy.add(rootProjectGid)
        do {
            var moreInHierarchy = false
            for (aProject in prjContexts) {
                if (!idsInHierarchy.contains(aProject.gid)
                        && idsInHierarchy.contains(aProject.partOf)) {
                    moreInHierarchy = true
                    idsInHierarchy.add(aProject.gid)
//                    if (aProject.partOf.isNotEmpty())
//                        idsInHierarchy.add(aProject.partOf)
                }
                if ((aProject.gid==rootProjectGid)
                        && aProject.partOf.isNotEmpty()) {
                    idsInHierarchy.add(aProject.partOf)
                }
            }
        } while (moreInHierarchy)
        return  idsInHierarchy
    }

    // Select projects in hierarchy
    private fun selectProjectsWithIds(prjContexts: List<PrjContext>, idsInHierarchy: Set<String>): List<PrjContext> {
        return prjContexts.filter { aProject -> idsInHierarchy.contains(aProject.gid) }
    }

    // Chart projects in hiearachy
    private fun prepareMarkDownDiag(projectsInHierarchy: List<PrjContext>): String {
        var markdownDiag = ""
        val lineSeparator = System.lineSeparator()
        markdownDiag += "classDef projectstyle fill:#${ChartColors.project},stroke:#333,stroke-width:2px;" + lineSeparator
        for (aProject in projectsInHierarchy) {
            markdownDiag += "id${aProject.gid}{{${aProject.name}}}" + lineSeparator
            markdownDiag += "class id${aProject.gid} projectstyle" + lineSeparator
            markdownDiag += "click id${aProject.gid} \"/project/${aProject.name}\" \"Goto to project.\"" + lineSeparator
            if (aProject.partOf.isNotEmpty()) {
                markdownDiag += "id${aProject.partOf}-- partOf -->id${aProject.gid}" + lineSeparator
            }
            var index= 0
            aProject.labels.let { labels ->
                if (!labels.isNullOrEmpty()) {
                    println("[$labels]")
                    markdownDiag += labels.split(",")
                        .joinToString(lineSeparator) { computeLabelString(aProject, index++, it, lineSeparator) }
                }
            }
        }
        return markdownDiag
    }

    private fun computeLabelString(
        aProject: PrjContext,
        index: Int,
        it: String,
        lineSeparator: String?
    ): String {
        val labelTokens = it.split(" ")
        return if (labelTokens.size==3)
            "id${aProject.gid}-- ${labelTokens[1]}-->l${aProject.gid}${index}(${labelTokens[2]})" + lineSeparator
        else
            "id${aProject.gid}-->l${aProject.gid}${index}($it)" + lineSeparator
    }

    private fun prepareMenu(userRole: String): String {
        var menu = ""
        if (userRole != User.ROLE.ANONYMOUS.name) {
            menu = """
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="/" >Home</a></li>
            <li class="breadcrumb-item active"><a href="/todos.html" >Goals</a></li>
        </ol>
    </nav> 
                """.trimIndent()
        }
        return menu
    }


}