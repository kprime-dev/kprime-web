package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.adapter.RdfStatement
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContextService

class ChartLabelsService(
    val prjContextService: PrjContextService,
    val rdfService: RdfService
) {


    //        markdownDiag+="""
    //                A-->B
    //                click A "http://www.github.com" "Tooltip for a callback"
    //        """.trimIndent()

    //        class Square~Shape~{
    //          int id
    //                List~int~ position
    //        setPoints(List~int~ points)
    //        getPoints() List~int~
    //    }

    fun chartLabelsHtml(traceName: String, traceFileName: String, projectName: String, labelFilter : String = ""): String {
        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val projectLocation = project.location
        rdfService.listStatements()
            .fold(
                onFailure = { return "not-found-labels ${it.message?:""}"},
                onSuccess = { listLabels ->
                    val dbLabels = dbLabelSet(listLabels)
                    var markdownDiag = ""
                    markdownDiag = "classDiagram" + System.lineSeparator()
                    markdownDiag = chartFunctionalLinks(listLabels, markdownDiag, labelFilter)
                    val markdownDiv = if (markdownDiag.isEmpty()) "" else
                        """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
        """.trimIndent()
                    var fileContent = ChartLabelsService::class.java.getResource("/public/chart-classes.html")?.readText()
                        ?: throw IllegalStateException()
                    fileContent = fileContent.replace("{{label}}","")
                    fileContent = fileContent.replace("{{labelFilterUrls}}",linkLabelFilterPages(dbLabels, projectName))
                    fileContent = fileContent.replace("{{traces}}", "")
                    fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
                    return fileContent
                }
            )
    }

    private fun dbLabelSet(listLabels: List<RdfStatement>): Set<String> {
        return listLabels.map { t -> chartSafeName(t.predicate) }.toSet()
    }

    private fun linkLabelFilterPages(dbLabels: Set<String>, contextName:String): String {
        var result = "<a href=\"/project/$contextName/chart/labels\">ALL</a> "
        for (label in dbLabels) { result += "| <a href=\"/project/$contextName/chart/labels/$label\">$label</a> " }
        return result
    }

    private fun chartFunctionalLinks(labels: List<RdfStatement>, markdownDiag: String, labelFilter: String): String {
        var markdownDiag1 = markdownDiag
        val labelsNoComment = labels.filter { !it.predicate.contains("comment") }
        val filteredLabels = if (labelFilter.isNotEmpty())
            labelsNoComment.filter { chartSafeName(it.predicate)==labelFilter }
            else labelsNoComment
        for (label in filteredLabels) {
            val sourceName = chartSafeName(label.subject)
            val targetName = chartSafeName(label.cobject)
            val predicate = chartSafeName(label.predicate)
            markdownDiag1 += "$targetName<..$sourceName : $predicate" + System.lineSeparator()
        }
        return markdownDiag1
    }

    companion object {
        internal fun chartSafeName(originalName: String): String {
            val newName = originalName.substringAfterLast("#").take(50)
                .replace("-","")
                .replace(" ","_")
                .replace("(","_")
                .replace(")","_")
                //.replace("_","")
                .replace("\"","")
                .replace("@","_")
                .replace("/","_")
                .replace(".","_")
                .replace("'","")
                .replace(":","")
                //.replace("@","")
            return if (newName.isEmpty()) "noname" else newName
        }
    }
}