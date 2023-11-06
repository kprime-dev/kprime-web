package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.adapter.RdfStatement
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.LabelField
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.support.alfaNumeric
import it.unibz.krdb.kprime.support.dashToCamelCase
import it.unibz.krdb.kprime.support.noBlank
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Schema
import unibz.cs.semint.kprime.domain.db.Table

class ChartClassesService(
    val traceService: TraceService,
    val dataService: DataService,
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

    fun chartClassesHtml(traceName: String, traceFileName: String, projectName: String): String {
        val traceNames = traceService.getTraceFileNames(traceName).sorted()
        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val projectLocation = PrjContextLocation(project.location)
        val database = dataService.getDatabase(projectLocation,traceName,traceFileName)
            .getOrElse { Database() }
            //.getOrElse { return "Database [$projectLocation.$traceName.$traceFileName] not found" }
        val dbLabels = dbLabelSet(database)
        var markdownDiag = ""
        if (database.schema.tables!=null && database.schema.tables!!.size > 0) {
            markdownDiag = "classDiagram" + System.lineSeparator()
            markdownDiag = chartTables(database, markdownDiag, label = "", projectLocation)
            markdownDiag = chartTableToDetails(database, markdownDiag, traceName, traceFileName, projectName)
            markdownDiag = chartForeignKeyLinks(database, markdownDiag)
            markdownDiag = chartDoubleIncLinks(database, markdownDiag)
            markdownDiag = chartFunctionalLinks(database, markdownDiag)
            markdownDiag = chartInclusionLinks(database, markdownDiag)
            markdownDiag = chartIsaLinks(database, markdownDiag)
            markdownDiag = chartMappingLinks(database, markdownDiag)
        }
        val markdownDiv = if (markdownDiag.isEmpty()) "" else
        """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
        """.trimIndent()
        var fileContent = ChartClassesService::class.java.getResource("/public/chart-classes.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{project.name}}", projectName)
        fileContent = fileContent.replace("{{label}}","")
        fileContent = fileContent.replace("{{labelFilterUrls}}",
            linkLabelFilterPages(dbLabels, traceName, traceFileName, projectName))
        fileContent = fileContent.replace("{{traces}}", linkTracePages(traceName, traceNames, projectName))
        fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
        return fileContent
    }

    fun chartColouredClassesHtml(traceName: String, traceFileName: String, projectName: String, orient:String): String {
        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val projectLocation = PrjContextLocation(project.location)
        val database = dataService.getDatabase(projectLocation,traceName,traceFileName)
            .getOrElse { Database() }
        val lineSeparator = System.lineSeparator()
        var markdownDiag = ""
        if (database.schema.tables!=null && database.schema.tables!!.size > 0) {
            markdownDiag = "flowchart $orient" + lineSeparator
            markdownDiag += "classDef role fill:#ff0000,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef relator fill:#ffaaaa,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef collective fill:#ffcccc,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef datatype fill:#ffffff,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef category fill:#eeeeee,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef quality fill:#aaaaff,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef kind fill:#bbbbff,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef subkind fill:#ddddff,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef type fill:#aaaaff,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef phaseMixin fill:#ffdddd,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef phase fill:#ffbbbb,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag += "classDef roleMixin fill:#ffdddd,stroke:#333,stroke-width:2px;" + lineSeparator
            markdownDiag = chartColouredTables(database, markdownDiag, rdfService, projectLocation)
        }
        val markdownDiv = if (markdownDiag.isEmpty()) "" else
            """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
        """.trimIndent()
        var fileContent = ChartClassesService::class.java.getResource("/public/chart-classes.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{project.name}}", projectName)
        fileContent = fileContent.replace("{{label}}","")
        fileContent = fileContent.replace("{{labelFilterUrls}}",linkLabelFilterStereotypes(projectName))
        fileContent = fileContent.replace("{{traces}}", "")
        fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
        return fileContent
    }

    private fun chartColouredTables(
        database: Database,
        markdownDiag: String,
        rdfService: RdfService,
        projectLocation: PrjContextLocation
    ): String {
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(projectLocation)
        var markdownDiag1 = markdownDiag
        val lineSeparator = System.lineSeparator()
        for (table in database.schema.tables()) {
            val rdfStatements = rdfService.findStatements(iriContext = "",
                LabelField(table.name), LabelField("ontouml:stereotype"), "_", rdfDataDir)
                .getOrDefault(emptyList())
            val rdfClassColour = if (rdfStatements.isNotEmpty())
                rdfStatements.map { it.cobject.substringAfterLast(':') }.last()
                else ""
            val camelTableName = chartSafeName(table.name)
            //println(camelTableName)
            if (rdfClassColour.isNotEmpty()) {
                markdownDiag1 += "  $camelTableName{{$rdfClassColour </br> ${table.name}}}"+lineSeparator
                markdownDiag1 += "  class ${camelTableName} $rdfClassColour"+lineSeparator
            } else {
                markdownDiag1 += "  ${camelTableName}"+lineSeparator
            }
        }
        for (mapping in database.mappings()) {
            val rdfStatements = rdfService.findStatements(iriContext = "",
                LabelField(mapping.name), LabelField("ontouml:stereotype"), "_", rdfDataDir)
                .getOrDefault(emptyList())
            val rdfClassColour = if (rdfStatements.isNotEmpty())
                rdfStatements.map { it.cobject.substringAfterLast(':') }.last()
            else ""
            if (rdfClassColour.isNotEmpty()) {
                markdownDiag1 += "  ${mapping.name}{{$rdfClassColour </br> ${mapping.name}}}"+lineSeparator
                markdownDiag1 += "  class ${mapping.name} $rdfClassColour"+lineSeparator
            } else {
                markdownDiag1 += "  ${mapping.name}"+lineSeparator

            }
        }
        markdownDiag1 = chartIsaLinks(database, markdownDiag1,"-.->")
        markdownDiag1 = chartMappingLinks(database, markdownDiag1,"-.->")
        //markdownDiag1 += chartFlowBinaryRelationships(database)
        markdownDiag1 += chartFlowForeignKeys(database)
        return markdownDiag1
    }

    fun chartLabelledClassesHtml(projectName: String, traceName: String, traceFileName: String, label:String): String {
        val traceNames = traceService.getTraceFileNames(traceName).sorted()
        val traceDir = traceService.getTraceDir()

        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val projectLocation = PrjContextLocation(project.location)
        val database = dataService.getDatabase(projectLocation,traceName,traceFileName)
            .getOrElse { Database() }
            //.getOrElse { return "Database [$projectLocation.$traceName.$traceFileName] not found" }

        val dbLabels = dbLabelSet(database)
        var markdownDiag = ""
        if (database.schema.tables!=null && database.schema.tables!!.size > 0) {
            markdownDiag = "classDiagram" + System.lineSeparator()
            markdownDiag = chartTables(database, markdownDiag, label, projectLocation)
            //markdownDiag = chartTableToDetails(database, markdownDiag, traceName, traceFileName)
            markdownDiag = chartForeignKeyLinks(database, markdownDiag)
            markdownDiag = chartDoubleIncLinks(database, markdownDiag)
            markdownDiag = chartFunctionalLinks(database, markdownDiag)
            markdownDiag = chartInclusionLinks(database, markdownDiag)
            markdownDiag = chartIsaLinks(database, markdownDiag)
        }
        val markdownDiv = if (markdownDiag.isEmpty()) "" else
        """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
        """.trimIndent()
        var fileContent = ChartClassesService::class.java.getResource("/public/chart-classes.html")?.readText()
            ?: throw IllegalStateException()
        fileContent = fileContent.replace("{{project.name}}", projectName)
        fileContent = fileContent.replace("{{label}}","/$label")
        fileContent = fileContent.replace("{{labelFilterUrls}}",linkLabelFilterPages(dbLabels, traceDir, traceFileName,projectName))
        fileContent = fileContent.replace("{{traces}}", linkTracePages(traceDir,traceNames, projectName))
        fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
        return fileContent
    }

    fun chartEntitiesClassesHtml(traceName: String, traceFileName: String, projectName: String): String {
        val traceNames = traceService.getTraceFileNames(traceName).sorted()
        val traceDir = traceService.getTraceDir()

        val project = prjContextService.projectByName(projectName)?: return "Project [$projectName.$traceName.$traceFileName] not found"
        val projectLocation = project.location
        val database = dataService.getDatabase(PrjContextLocation(projectLocation),traceName,traceFileName)
            .getOrElse { Database() }
            //.getOrElse { return "Database [$projectLocation.$traceName.$traceFileName] not found" }

        val dbLabels = dbLabelSet(database)
        var markdownDiag = ""
        if (database.schema.tables!=null && database.schema.tables!!.size > 0) {
            markdownDiag = "classDiagram" + System.lineSeparator()
            markdownDiag = chartEntityTables(database, markdownDiag)
//            markdownDiag = chartTableToDetails(database, markdownDiag, traceName, traceFileName)
//            markdownDiag = chartForeignKeyLinks(database, markdownDiag)
//            markdownDiag = chartDoubleIncLinks(database, markdownDiag)
//            markdownDiag = chartFunctionalLinks(database, markdownDiag)
//            markdownDiag = chartInclusionLinks(database, markdownDiag)
//            markdownDiag = chartIsaLinks(database, markdownDiag)
        }
        val markdownDiv = if (markdownDiag.isEmpty()) "" else
        """
            <div class="mermaid animated bounceInRight">
             $markdownDiag
            </div>
        """.trimIndent()
        var fileContent = ChartClassesService::class.java.getResource("/public/chart-classes.html")?.readText()
            ?: throw IllegalStateException("Resource /public/chart-classes.html not found.")
        fileContent = fileContent.replace("{{project.name}}", projectName)
        fileContent = fileContent.replace("{{label}}","")
        fileContent = fileContent.replace("{{labelFilterUrls}}",linkLabelFilterPages(dbLabels, traceDir, traceFileName,projectName))
        fileContent = fileContent.replace("{{traces}}", linkTracePages(traceDir,traceNames,projectName))
        fileContent = fileContent.replace("{{markdownDiv}}", markdownDiv)
        return fileContent
    }

    private fun dbLabelSet(database: Database) =
            database.schema.tables?.flatMap { t -> t.labelsAsString().split(",") }?.toSet() ?: emptySet()

    private fun linkLabelFilterPages(dbLabels: Set<String>, traceDir: String, traceFileName: String, contextName:String): String {
        var result = "<a href=\"/context/$contextName/chart/classes/$traceDir/$traceFileName\">ALL</a> "
        for (label in dbLabels) { result += "| <a href=\"/context/$contextName/chart/classes/$label/$traceDir/$traceFileName\">$label</a> " }
        result += "| <a href=\"/context/$contextName/chart/entities/$traceDir/$traceFileName\">ENTITIES</a> "
        result += "| <a href=\"/project/$contextName/chart/classes?orient=TD\">STEREOTYPES</a> "
        return result
    }

    private fun linkLabelFilterStereotypes(contextName:String): String {
        var result = "<a href=\"/project/$contextName/chart/classes?orient=TD\">TOP-DOWN</a> "
        result += "| <a href=\"/project/$contextName/chart/classes?orient=BT\">BUTTOM-UP</a> "
        result += "| <a href=\"/project/$contextName/chart/classes?orient=LR\">LEFT-RIGHT</a> "
        result += "| <a href=\"/project/$contextName/chart/classes?orient=RL\">RIGHT-LEFT</a> "
        return result
    }

    private fun linkTracePages(traceDir:String, names:List<String>,contextName: String ): String {
        var result = ""
        for (page in names) {
            if (page.contains("tracedb"))
                result += "| <a href=\"/context/$contextName/chart/classes/$traceDir/$page\">$page</a> "
        }
        return result
    }

    private fun chartTableToDetails(
        database: Database,
        markdownDiag: String,
        traceName: String,
        traceFileName: String,
        projectName: String
    ): String {
        var markdownDiag1 = markdownDiag
        for (table in database.schema.tables()) {
            if (!database.schema.isBinaryRelation(table.name)) {
                val tableName = chartSafeName(table.name)
                //markdownDiag1 += "link $tableName \"/chart/${traceName}/${traceFileName}/${table.name}\" \"Tooltip for a callback\"" + System.lineSeparator()
                markdownDiag1 += "link $tableName \"/context/${projectName}/chart/focus/${traceName}${traceFileName}/${table.name}\" \"Tooltip for a callback\"" + System.lineSeparator()
            }
        }
        return markdownDiag1
    }

    private fun chartForeignKeyLinks(database: Database, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val foreignKeys = database.schema.foreignKeys()
        for (foreign in foreignKeys) {
            //print("${foreign.name} ${database.schema.isBinaryRelation(foreign.source.table)} ${database.schema.isBinaryRelation(foreign.target.table)}")
            if (!isLinkToBinaryRelation(database.schema,foreign)) {
                var foreignSourceName = chartSafeName(foreign.source.table)
                if (foreignSourceName.isEmpty()) foreignSourceName = chartSafeName(foreign.source.name)
                var foreignTargetName = chartSafeName(foreign.target.table)
                if (foreignTargetName.isEmpty()) foreignTargetName = chartSafeName(foreign.target.name)
                markdownDiag1 += "$foreignSourceName-->$foreignTargetName : Foreign" + System.lineSeparator()
            }
        }
        return markdownDiag1
    }

    private fun isLinkToBinaryRelation(schema: Schema, foreign: Constraint): Boolean {
        return schema.isBinaryRelation(foreign.source.table) || schema.isBinaryRelation(foreign.target.table)
    }

    private fun chartDoubleIncLinks(database: Database, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val doubleIncs = database.schema.doubleIncs()
        for (doubleInc in doubleIncs) {
            var foreignSourceName = chartSafeName(doubleInc.source.table)
            if (foreignSourceName.isEmpty()) foreignSourceName = chartSafeName(doubleInc.source.name)
            var foreignTargetName = chartSafeName(doubleInc.target.table)
            if (foreignTargetName.isEmpty()) foreignTargetName = chartSafeName(doubleInc.target.name)
            markdownDiag1 += "$foreignSourceName--$foreignTargetName : Double" + System.lineSeparator()
        }
        return markdownDiag1
    }

    private fun chartFunctionalLinks(database: Database, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val functionals = database.schema.functionals()
        for (functional in functionals) {
            var foreignSourceName = chartSafeName(functional.source.table)
            if (foreignSourceName.isEmpty()) foreignSourceName = chartSafeName(functional.source.name)
            var foreignTargetName = chartSafeName(functional.target.table)
            if (foreignTargetName.isEmpty()) foreignTargetName = chartSafeName(functional.target.name)
            markdownDiag1 += "$foreignTargetName<..$foreignSourceName : Functional" + System.lineSeparator()
        }
        return markdownDiag1
    }

    private fun chartInclusionLinks(database: Database, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val inclusions = database.schema.inclusions()
        for (inclusion in inclusions) {
            var foreignSourceName = chartSafeName(inclusion.source.table)
            if (foreignSourceName.isEmpty()) foreignSourceName = chartSafeName(inclusion.source.name)
            var foreignTargetName = chartSafeName(inclusion.target.table)
            if (foreignTargetName.isEmpty()) foreignTargetName = chartSafeName(inclusion.target.name)
            markdownDiag1 += "$foreignTargetName<--$foreignSourceName : Inclusion" + System.lineSeparator()
        }
        return markdownDiag1
    }

    private fun chartIsaLinks(database: Database, markdownDiag: String, symbol: String = "<|--"): String {
        var markdownDiag1 = markdownDiag
        for (table in database.schema.tables()) {
            val tableParent = table.parent ?: continue
            for (parentName in tableParent.split(",")) {
                val safeTableParent = chartSafeName(parentName)
                val safeTableChild = chartSafeName(table.name)
                markdownDiag1 += "${safeTableParent}${symbol}${safeTableChild}" + System.lineSeparator()
            }
        }
        return markdownDiag1
    }

    private fun chartMappingLinks(database: Database, markdownDiag: String, symbol: String = "<.."): String {
        var markdownDiag1 = markdownDiag
        for (mapping in database.mappings()) {
            val tableChildren = mapping.select.from.tableName
            for (childName in tableChildren.split(",")) {
                val safeTableChild = chartSafeName(childName)
                val safeTableParent = chartSafeName(mapping.name)
                markdownDiag1 += "${safeTableChild}${symbol}${safeTableParent}" + System.lineSeparator()
            }
        }
        return markdownDiag1
    }

    private fun chartFlowForeignKeys(database: Database): String {
        var markdownDiag1 = ""
        for (fkey in database.schema.constraintsByType(Constraint.TYPE.FOREIGN_KEY)) {
            markdownDiag1 += "${chartSafeName(fkey.source.name)}-->|fk| ${chartSafeName(fkey.target.name)} " + System.lineSeparator()
        }
        return markdownDiag1
    }

    private fun chartFlowBinaryRelationships(database: Database): String {
        var markdownDiag1 = ""
        for (table in database.schema.tables()) {
            val foreignKeysTable = database.schema.foreignsWithTarget(table.name)
            if (foreignKeysTable.size == 2) {
                val fkeySourceName = chartSafeName(destination(foreignKeysTable[0], table))
                val fkeyTargetName = chartSafeName(destination(foreignKeysTable[1], table))
                //markdownDiag1 += "$fkeySourceName-->|${binaryRelationName(table.name)}| $fkeyTargetName " + System.lineSeparator()
                markdownDiag1 += "$fkeySourceName---${binaryRelationName(table.name)} " + System.lineSeparator()
                markdownDiag1 += "${binaryRelationName(table.name)}-->$fkeyTargetName " + System.lineSeparator()
            }
        }
        return markdownDiag1
    }

    private fun chartEntityTables(database: Database, markdownDiag: String): String {
        var markdownDiag1 = markdownDiag
        val tables = database.schema.tables()
        for (table in tables) {
            if (!table.hasLabel("relation")) markdownDiag1 += "     class ${chartSafeName(table.name)} { }" + System.lineSeparator()
        }
        return markdownDiag1
    }

    private fun chartTables(
        database: Database,
        markdownDiag: String,
        label: String,
        projectLocation: PrjContextLocation
    ): String {
        var markdownDiag1 = markdownDiag
        val tables = database.schema.tables()
        val tablesLabelled = if (label.isNotEmpty()) tables.filter { it.hasLabel(label) } else tables
        val tablesNotLabelled = tables.filter { !it.hasLabel(label) }
        println("tablesNotLabelled:$tablesNotLabelled")
        for (table in tablesNotLabelled) {
            val tableName = chartSafeName(table.name)
            val rdfStatements = rdfService.findStatements(iriContext = "",
                LabelField(tableName), LabelField("_"), "_",
                rdfDataDir = RdfService.getPrjContextRdfDataDir(projectLocation) )
                .getOrDefault(emptyList())
            val rdfLabels = rdfStatements.joinToString(System.lineSeparator()) { rdfLabel(it) } +System.lineSeparator()
            println("<<rdfLabels>>[$rdfLabels]")
            markdownDiag1 += """class $tableName { 
                                    $rdfLabels
                     }
                     cssClass "$tableName" labelled
                     
                     """.trimIndent()
        }
        markdownDiag1 += System.lineSeparator()
        println("tablesLabelled:$tablesLabelled")
        for (table in tablesLabelled) {
            val foreignKeysTable = database.schema.constraintsByTable(table.name)
                .filter { it.type == Constraint.TYPE.FOREIGN_KEY.name }
            if (foreignKeysTable.size == 2) {
                    val sourceCardinality = table.columns[0].cardinality ?:"*"
                    val targetCardinality = table.columns[1].cardinality ?:"*"
                    val fkeySourceName = chartSafeName(destination(foreignKeysTable[0],table))
                    val fkeyTargetName = chartSafeName(destination(foreignKeysTable[1],table))
                     markdownDiag1 += "$fkeySourceName \"$sourceCardinality\" --o \"$targetCardinality\" $fkeyTargetName : ${chartSafeName(table.name)}" + System.lineSeparator()
            }
            else {
                val tableName = chartSafeName(table.name)
//to fix                markdownDiag1 += "cssClass \"${tableName}\" labelled" + System.lineSeparator()
                if (label.isEmpty()) {
                    markdownDiag1 += "class ${tableName}:::labelled { }" + System.lineSeparator()
                } else {
                    markdownDiag1 += "class ${tableName}:::labelled {" + System.lineSeparator()
                    if (table.labelsAsString().isNotEmpty()) {
                        markdownDiag1 += "  labels : ${table.labelsAsString().uppercase()}" + System.lineSeparator()
                    }
                    for (att in table.columns) {
                        val attName = chartSafeName(att.name)
                        val attDbType = chartSafeName(att.dbtype?:"")
                        markdownDiag1 += "  $attName : $attDbType" + System.lineSeparator()
                    }
                    for (fkey in foreignKeysTable) {
                        val fkeySourceName = chartSafeName(fkey.source.table)
                        val fkeyTargetName = chartSafeName(fkey.target.table)
                        markdownDiag1 += "  ${fkeySourceName}_$fkeyTargetName()" + System.lineSeparator()
                    }
                    markdownDiag1 += "}" + System.lineSeparator()
                }
            }
        }
        return markdownDiag1
    }

    private fun rdfLabel(it: RdfStatement) =
        "_${it.predicate.alfaNumeric("_")}:${it.cobject.alfaNumeric("_")}_"

    private fun binaryRelationName(name: String): String {
        println("ChartClassesService.chartTables.binaryRelationName:[$name]")
        return name.split("_").dropLast(1).drop(1).joinToString(" ")
    }

    private fun destination(fkey: Constraint, table: Table): String {
        return if (fkey.source.table==table.name) fkey.target.table
        else fkey.source.table
    }

    private fun chartSafeName(originalName: String): String {
        return originalName.dashToCamelCase().noBlank().replace(":","_")
    }
}