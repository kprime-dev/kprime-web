package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.trace.TraceService
import unibz.cs.semint.kprime.domain.db.Database

class ChartEntityService(
    val traceService: TraceService,
    val dataService: DataService,
    val prjContextService: PrjContextService
) {

    fun chartEntityHtml(traceName: String, traceFileName: String, entityName: String,  projectName: String): String {

        val prjContext = prjContextService.projectByName(projectName) ?: PrjContext("","")

        val database = dataService.getDatabase(PrjContextLocation(prjContext.location), traceName, traceFileName)
            .getOrElse { Database() }
            //.getOrElse { return "Database [${prjContext.location}.$traceName.$traceFileName] not found" }

        var markdownDiag = ""
        val table = database.schema.table(entityName)
        if (table != null) {
            markdownDiag += "classDef orange fill:#f96,stroke:#333,stroke-width:1px;" + System.lineSeparator()
            markdownDiag += "classDef green fill:#9f6,stroke:#333,stroke-width:2px;" + System.lineSeparator()
            markdownDiag += "classDef white fill:#fff,stroke:#333,stroke-width:3px;" + System.lineSeparator()
            if (table.parent.isNullOrEmpty()) {
                markdownDiag += "${entityName}"+ System.lineSeparator()
                markdownDiag += "click ${entityName} \"/project/${projectName}/dictionary/${entityName}\" \"Go to entity page.\"" + System.lineSeparator()
            } else {
                var tableCondition = table.condition; if (tableCondition.isEmpty()) tableCondition = "is-a"
                markdownDiag += "${entityName}==>|${tableCondition}|_${table.parent}([${table.parent}])" + System.lineSeparator()
                markdownDiag += "class _${table.parent} orange" + System.lineSeparator()
                markdownDiag += "click _${table.parent} \"/context/${projectName}/chart/${traceName}${traceFileName}/${table.parent}\" \"Go to parent chart.\"" + System.lineSeparator()
            }
            val columns = table.columns
            val keys = database.schema.keyCols(entityName).map { c -> c.name }
            val skey = database.schema.keySurrogate(entityName)
            for (col in columns) {
                if (keys.contains(col.name)) {
                    markdownDiag += "${entityName}-- key -->_${col.name.take(17)}([_${col.name.take(17)} : ${col.dbtype}])" + System.lineSeparator()
                    markdownDiag += "class _${col.name.take(17)} white" + System.lineSeparator()
                } else if (skey!=null && skey.source.columns.first().name==col.name) {
                        markdownDiag += "${entityName}-- sid -->_${col.name.take(17)}([_${col.name.take(17)} : ${col.dbtype}])" + System.lineSeparator()
                        markdownDiag += "class _${col.name.take(17)} white" + System.lineSeparator()
                } else {
                    markdownDiag += "${entityName}-->_${col.name.take(17)}([_${col.name.take(17)} : ${col.dbtype}])" + System.lineSeparator()
                }
            }
            val foreignKeysTable = database.schema.foreignsWithSource(entityName)
            for (fkey in foreignKeysTable) {
                markdownDiag += "${fkey.source.table}==>|of|_${fkey.target.table}([_${fkey.target.table}])" + System.lineSeparator()
                markdownDiag += "click _${fkey.target.table} \"/context/${projectName}/chart/${traceName}${traceFileName}/${fkey.target.table}\" \"Tooltip for a callback\"" + System.lineSeparator()
                markdownDiag += "class _${fkey.target.table} orange" + System.lineSeparator()
                for (colName in fkey.left().map { c -> c.name }) {
                    markdownDiag += "_${fkey.target.table}-- fsource -->_${colName}" + System.lineSeparator()
                }
            }
            val foreignTargets = database.schema.foreignsWithTarget(entityName)
            for (fkey in foreignTargets) {
                markdownDiag += "${entityName}==>|has|_${fkey.source.table}([_${fkey.source.table}])" + System.lineSeparator()
                markdownDiag += "click _${fkey.source.table} \"/context/${projectName}/chart/${traceName}${traceFileName}/${fkey.source.table}\" \"Tooltip for a callback\"" + System.lineSeparator()
                markdownDiag += "class _${fkey.source.table} orange" + System.lineSeparator()
                for (colName in fkey.right().map { c -> c.name }) {
                    markdownDiag += "_${fkey.source.table}-- ftarget -->_${colName}" + System.lineSeparator()
                }
            }
            val functionals = database.schema.functionalsTable(entityName)
            for (functional in functionals) {
                for (col in functional.left())
                    markdownDiag += "_${col.name}-. funl .->${functional.name}" + System.lineSeparator()
                for (col in functional.right())
                    markdownDiag += "${functional.name}-. funr .->_${col.name}" + System.lineSeparator()
            }
            val multivals = database.schema.multivalued(entityName)
            for (multival in multivals) {
                for (col in multival.left())
                    markdownDiag += "_${col.name}-. multil .->${multival.name}" + System.lineSeparator()
                for (col in multival.right())
                    markdownDiag += "${multival.name}-. multir .->_${col.name}" + System.lineSeparator()
            }
            val doubles = database.schema.doubleIncs(entityName)
            for (doubleInc in doubles) {
                for (col in doubleInc.left())
                    markdownDiag += "_${col.name}<-- double -->${doubleInc.name}" + System.lineSeparator()
                for (col in doubleInc.right())
                    markdownDiag += "${doubleInc.name}<-- double -->_${col.name}" + System.lineSeparator()
                markdownDiag += if (doubleInc.source.table==entityName) {
                    "click ${doubleInc.name} \"/context/${projectName}/chart/${traceName}${traceFileName}/${doubleInc.target.table}\" \"Click Go to\"" + System.lineSeparator()
                } else {
                    "click ${doubleInc.name} \"/context/${projectName}/chart/${traceName}${traceFileName}/${doubleInc.source.table}\" \"Click Go to\"" + System.lineSeparator()
                }
            }
            val inclusions = database.schema.inclusions(entityName)
            for (doubleInc in inclusions) {
                for (col in doubleInc.left())
                    markdownDiag += "_${col.name}<-- double -->${doubleInc.name}" + System.lineSeparator()
                for (col in doubleInc.right())
                    markdownDiag += "${doubleInc.name}<-- double -->_${col.name}" + System.lineSeparator()
                markdownDiag += if (doubleInc.source.table==entityName) {
                    "click ${doubleInc.name} \"/context/${projectName}/chart/${traceName}${traceFileName}/${doubleInc.target.table}\" \"Click Go to\"" + System.lineSeparator()
                } else {
                    "click ${doubleInc.name} \"/context/${projectName}/chart/${traceName}${traceFileName}/${doubleInc.source.table}\" \"Click Go to\"" + System.lineSeparator()
                }
            }
        }

        var fileContent = ChartEntityService::class.java.getResource("/public/chart-entity.html").readText()

        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        return fileContent
    }

}