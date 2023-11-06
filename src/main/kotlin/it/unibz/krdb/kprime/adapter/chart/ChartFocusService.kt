package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.trace.TraceService
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table

class ChartFocusService(val traceService: TraceService, val dataService: DataService, val prjContextService: PrjContextService) {

    fun chartFocusHtml(projectName: String, traceName: String, traceFileName: String, entityName: String): String {

        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val database = dataService.getDatabase(PrjContextLocation(project.location), traceName, traceFileName)
            .getOrElse { Database() }
            //.getOrElse { return "Database [${project.location}.$traceName.$traceFileName] not found" }

        val originTable = database.schema.table(entityName) ?: return "no-table"

        var markdownDiag = "classDiagram" + System.lineSeparator()
        markdownDiag = chartTable(originTable,markdownDiag)
        markdownDiag = chartTableToDetails(originTable, markdownDiag, traceName, traceFileName,projectName)
        markdownDiag = chartIsaLinks(database,originTable,markdownDiag, traceName, traceFileName, projectName)
        markdownDiag = chartBinaryLinks(database,originTable,markdownDiag, traceName, traceFileName,projectName)

        val markdownDiv = if (markdownDiag.isEmpty()) "" else
            """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
            """
        var fileContent = ChartFocusService::class.java.getResource("/public/chart-classes.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{project.name}}",projectName)
        fileContent = fileContent.replace("{{label}}","")
        fileContent = fileContent.replace("{{labelFilterUrls}}","")
        fileContent = fileContent.replace("{{traces}}", "")
        fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
        return fileContent
    }

    private fun chartBinaryLinks(database: Database, originTable: Table, markdownDiag: String, traceName: String, traceFileName: String, projectName:String): String {
        var markdownDiag1 = markdownDiag
        val originTableName = originTable.name
        for (table in database.schema.tables()) {
            val tableName = table.name
            val foreignKeysTable = database.schema.foreignsWithTarget(tableName)
            if (foreignKeysTable.size == 2 && (isOrigin(foreignKeysTable[0],originTableName) || isOrigin(foreignKeysTable[1],originTableName))) {
                val fkeySourceName = chartSafeName(destination(foreignKeysTable[0], tableName))
                val fkeyTargetName = chartSafeName(destination(foreignKeysTable[1], tableName))
                markdownDiag1 += "$fkeySourceName-->$fkeyTargetName : ${binaryRelationName(tableName)}" + System.lineSeparator()
                val relatedTableName = if (fkeySourceName==originTableName) fkeyTargetName else fkeySourceName
                markdownDiag1 += chartTableToBrowse(relatedTableName,traceName,traceFileName, projectName)
            }
        }
        return markdownDiag1
    }

    private fun isOrigin(fkey: Constraint, tableName: String): Boolean {
        return fkey.source.table==tableName || fkey.target.table==tableName
    }

    private fun destination(fkey: Constraint, tableName: String): String {
        if (fkey.source.table==tableName) return fkey.target.table
        else return fkey.source.table
    }

    private fun binaryRelationName(name: String): String {
        return name.split("_").dropLast(1).drop(1).joinToString(" ")
    }


    private fun chartTable(table: Table, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val tableName = chartSafeName(table.name)
        markdownDiag1 += "cssClass \"${tableName}\" labelled" + System.lineSeparator()
        markdownDiag1 += "class ${tableName}:::labelled {" + System.lineSeparator()
        if (table.labelsAsString().isNotEmpty()) {
            markdownDiag1 += "  labels : ${table.labelsAsString().uppercase()}" + System.lineSeparator()
        }
        for (att in table.columns) {
            val attName = chartSafeName(att.name)
            val attDbType = chartSafeName(att.dbtype?:"")
            markdownDiag1 += "  $attName : $attDbType" + System.lineSeparator()
        }
        markdownDiag1 += "}" + System.lineSeparator()
        return markdownDiag1
    }

    private fun chartSafeName(originalName: String): String {
        return originalName.replace("-","_").replace(":","_")
    }

    private fun chartTableToDetails(table: Table, markdownDiag: String, traceName: String, traceFileName: String, projectName:String): String {
        var markdownDiag1 = markdownDiag
        val tableName = chartSafeName(table.name)
        markdownDiag1 += "link $tableName \"/context/${projectName}/chart/${traceName}${traceFileName}/${table.name}\" \"Click to details\"" + System.lineSeparator()
        return markdownDiag1
    }

    private fun chartTableToBrowse(tableName:String, traceName: String, traceFileName: String, projectName:String): String {
        return "link $tableName \"/context/${projectName}/chart/focus/${traceName}${traceFileName}/${tableName}\" \"Click to browse\"" + System.lineSeparator()
    }


    private fun chartIsaLinks(database: Database, originTable: Table, markdownDiag: String, traceName: String, traceFileName: String, projectName:String): String {
        var markdownDiag1 = markdownDiag
        // draw parents
        val tableParents = originTable.parent ?: ""
        if (tableParents.isNotEmpty()) {
            for (parentName in tableParents.split(",")) {
                val safeTableParent = chartSafeName(parentName)
                val safeTableChild = chartSafeName(originTable.name)
                markdownDiag1 += "${safeTableParent}<|--${safeTableChild}" + System.lineSeparator()
                markdownDiag1 += chartTableToBrowse(safeTableParent,traceName,traceFileName, projectName)
            }
        }
        // draw children
        for (table in database.schema.tables()) {
            val tableParent = table.parent ?: continue
            for (parentName in tableParent.split(",")) {
                if (parentName==originTable.name) {
                    val safeTableParent = chartSafeName(originTable.name)
                    val safeTableChild = chartSafeName(table.name)
                    markdownDiag1 += "${safeTableParent}<|--${safeTableChild}" + System.lineSeparator()
                    markdownDiag1 += chartTableToBrowse(safeTableChild,traceName,traceFileName, projectName)
                }
            }
        }
        return markdownDiag1
    }

}