package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.trace.TraceService
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import java.lang.IllegalStateException

class ChartEntitiesService(val traceService: TraceService) {

    fun chartEntitiesHtml(traceName: String, traceFileName: String): String {
        TODO("database access via projectLocation $traceName $traceFileName")
    }

    fun chartEntitiesHtmlTOREFACT(traceName: String, traceFileName: String): String {

        val traceFileContent = traceService.getTraceFileContent(traceName, traceFileName)
        val database = XMLSerializerJacksonAdapter().deserializeDatabase(traceFileContent)

        var markdownDiag = ""

        //        var markdownDiag="""
        //                A-->B
        //                click A "http://www.github.com" "Tooltip for a callback"
        //        """.trimIndent()
        for (table in database.schema.tables()) {
            markdownDiag += table.name + System.lineSeparator()
            markdownDiag += "click ${table.name} \"/chart/${traceName}/${traceFileName}/${table.name}\" \"Tooltip for a callback\"" + System.lineSeparator()
            //markdownDiag+="${table.name}-->${table.name}"+System.lineSeparator()
        }

        val foreignKeys = database.schema.foreignKeys()
        var linkIndex = 0
        for (foreign in foreignKeys) {
            linkIndex++
            markdownDiag += "${foreign.source.table}-->${foreign.target.table}" + System.lineSeparator()
            //markdownDiag += "click ${foreign.source.table} \"/chart/${traceName}/${traceFileName}/${foreign.source.table}\" \"foreign\"" + System.lineSeparator()
            //markdownDiag += "click ${foreign.target.table} \"/chart/${traceName}/${traceFileName}/${foreign.target.table}\" \"foreign\"" + System.lineSeparator()
        }
        val functionals = database.schema.functionals()
        for (functional in functionals) {
            linkIndex++
            markdownDiag += "${functional.source.table}==>${functional.target.table}" + System.lineSeparator()
            //markdownDiag += "click ${functional.source.table} \"/chart/${traceName}/${traceFileName}/${functional.source.table}\" \"functional\"" + System.lineSeparator()
            //markdownDiag += "click ${functional.target.table} \"/chart/${traceName}/${traceFileName}/${functional.target.table}\" \"functional\"" + System.lineSeparator()
        }
        val doubleIncs = database.schema.doubleIncs()
        val doubleIncsIndex = mutableListOf<Int>()
        for (doubleInc in doubleIncs) {
            linkIndex++
            markdownDiag += "${doubleInc.source.table}<==>${doubleInc.target.table}" + System.lineSeparator()
            //markdownDiag += "click ${doubleInc.source.table} \"/chart/${traceName}/${traceFileName}/${doubleInc.source.table}\" \"double\"" + System.lineSeparator()
            //markdownDiag += "click ${doubleInc.target.table} \"/chart/${traceName}/${traceFileName}/${doubleInc.target.table}\" \"double\"" + System.lineSeparator()
            doubleIncsIndex.add(linkIndex)
        }
        val inclusions = database.schema.inclusions()
        for (inclusion in inclusions) {
            linkIndex++
            markdownDiag += "${inclusion.source.table}-.->${inclusion.target.table}" + System.lineSeparator()
            //markdownDiag += "click ${inclusion.source.table} \"/chart/${traceName}/${traceFileName}/${inclusion.source.table}\" \"inclusion\"" + System.lineSeparator()
            //markdownDiag += "click ${inclusion.target.table} \"/chart/${traceName}/${traceFileName}/${inclusion.target.table}\" \"inclusion\"" + System.lineSeparator()
        }
//        for (linkInd in doubleIncsIndex) {
//            markdownDiag += "linkStyle $linkInd stroke:#f00,stroke-width:4px,color:red;" + System.lineSeparator()
//        }

        //        markdownDiag+="""
        //                A-->B
        //                click A "http://www.github.com" "Tooltip for a callback"
        //        """.trimIndent()

        var fileContent = ChartEntitiesService::class.java.getResource("/public/chart-entity.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        return fileContent
    }

}