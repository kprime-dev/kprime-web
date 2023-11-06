package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.trace.TraceService

class ChartStoryService(val traceService: TraceService) {

    fun  getChartStoryActivitiesHtml(prjContext: PrjContext, traceName:String, storyFileName:String): String {
        val storyLines = traceService.getTraceFileLines(prjContext.location,traceName,storyFileName)
        val linesToChart = this.drawableLines(storyLines)
        var markdownDiag="graph TD"+System.lineSeparator()
        markdownDiag+="classDef green fill:#9f6,stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef yellow fill:#ff0,stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef red fill:#f00,stroke:#333,stroke-width:2px;"+System.lineSeparator()

        markdownDiag+="classDef readmodelstyle fill:#${ChartColors.readmodel},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef actorstyle fill:#${ChartColors.user},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef commandstyle fill:#${ChartColors.command},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef aggregatestyle fill:#${ChartColors.aggregate},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef externalstyle fill:#${ChartColors.external},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef eventstyle fill:#${ChartColors.event},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef policystyle fill:#${ChartColors.policy},stroke:#333,stroke-width:2px;"+System.lineSeparator()
        markdownDiag+="classDef goalstyle fill:#${ChartColors.policy},stroke:#333,stroke-width:2px;"+System.lineSeparator()

        markdownDiag+="id1(START)-->"
        var index = 1
        for (line in linesToChart) {
            var pair = storyAddTitleLine(line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddConditionLine(line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddSourceLine(line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("readmodel",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("actor",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("command",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("aggregate",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("external",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("event",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddTaggedLine("policy",line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
            pair = storyAddUrlLine(line, index, markdownDiag); index = pair.first; markdownDiag = pair.second
        }
        markdownDiag+="idEnd(END);"+System.lineSeparator()
        var fileContent = ChartStoryService::class.java.getResource("/public/chart-story.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{markdownDiag}}",markdownDiag)
        return fileContent
    }

    fun  getChartStoryHtml(prjContext: PrjContext, traceName:String, storyFileName:String): String {
        val storyLines = traceService.getTraceFileLines(prjContext.location,traceName,storyFileName)
        val linesToChart = this.drawableLines(storyLines)
        var markdownDiag=""
        for (line in linesToChart) {
            if (!line.trim().startsWith("#"))
                markdownDiag += line +System.lineSeparator()
        }
        var fileContent = ChartStoryService::class.java.getResource("/public/chart-story.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{markdownDiag}}",markdownDiag)
        return fileContent
    }

    fun  getAsciidocHtml(prjContext: PrjContext, traceName:String, storyFileName:String): String {
        val storyLines = traceService.getTraceFileLines(prjContext.location,traceName,storyFileName)
        val linesToChart = this.drawableLines(storyLines)
        var markdownDiag=""
        for ((index,line) in linesToChart.withIndex()) {
            if (index==linesToChart.size-1) {
                markdownDiag += "'" + line + "'" +System.lineSeparator()
            } else {
                markdownDiag += "'" + line + "pass:[</br>]'+" +System.lineSeparator()
            }
        }
        var fileContent = ChartStoryService::class.java.getResource("/public/asciidoc.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{asciidocText}}",markdownDiag)
        return fileContent
    }

    private fun drawableLines(storyLines: List<String>) =
        storyLines.filterNot { it.startsWith("--") || it.startsWith("#") || it.startsWith("```") }

}