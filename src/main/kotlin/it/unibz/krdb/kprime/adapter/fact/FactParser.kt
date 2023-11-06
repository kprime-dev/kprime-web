package it.unibz.krdb.kprime.adapter.fact

import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.support.dashToCamelCase
import unibz.cs.semint.kprime.domain.db.*
import unibz.cs.semint.kprime.domain.ddl.ChangeSet


object FactParser {

    fun parseFact(command:String, lastSubject: String, changeSet: ChangeSet, database:Database): TraceCmdResult {
        val tokens = command.trim().split(" ")
        val tokensMap =
            parseAsTripletMap(command) ?: return TraceCmdResult() failure "Required at least two arguments."
        var subj = tokensMap[TripletRole.SUBJECT] ?: error("No subject")
        if (subj.isEmpty()) error("subject empty")
        if (subj == "...") subj = lastSubject
        val pred = tokensMap[TripletRole.PREDICATE] ?: error("No predicate")
        val cobj = tokensMap[TripletRole.COBJECT]
        val subjPrefix = tokensMap[TripletRole.SUBJECT_PREFIX]
        val cobjPrefix = tokensMap[TripletRole.COBJECT_PREFIX]
        //if (tokensMap.size == 2) return parseUnaryRelationship(database, subj, pred)
        val cobjs = cobj?.split(" ") ?: emptyList() // drops cmd,sub,pred
        val subjectTable = database.schema.table(subj) ?:
                                        database.schema.relation(subj) ?:
                                        parseNewTable(subj, database, changeSet)
        val result: TraceCmdResult = when (pred) {
            "as" -> parseAs(cobjs, subjectTable)
            "has" -> parseHas(cobjs, database, subjectTable)
            //"has-label" -> parseLabels(cobjs, database, subjectTable)
            "has-key" -> parseHasKey(cobjs, subjectTable, database, subj)
            "has-id" -> parseHasId(cobjs, subjectTable, database, subj)
            "subset" -> parseSubset(cobjs, subjectTable, database, subj)
            "excludes" -> parseExcludes(cobjs, subjectTable, database, subj)
            "is-a" -> parseIsA(cobjs, database, subj)
            "is-covered" -> parseIsCovered(cobjs, database, subj)
            "is-partitioned" -> parseIsPartitioned(cobjs, database, subj)
            "is-exclusively" -> parseIsExclusively(cobjs, database, subj)
            "equals" -> parseEquals(cobjs, database, subj)
            else -> parseRelationship(database, subj, pred, cobj, subjPrefix, cobjPrefix)
        }
        result.payload = subj
        return result
    }

    private fun parseEquals(cobjs: List<String>, database: Database, sourceTableName: String): TraceCmdResult {
        val targetTableName = cobjs[0]
        val colName = cobjs[1]
        val sourceRelation = database.schema.relation(sourceTableName) ?:
         return TraceCmdResult() failure "Source relation not found."
        val targetRelation = database.schema.relation(targetTableName) ?:
        return TraceCmdResult() failure "Target relation not found."
        val col = sourceRelation?.colByName(colName) ?:
        return TraceCmdResult() failure "Role not found."
        database.schema.addEquals(sourceTableName,targetTableName, setOf(col))
        return TraceCmdResult() message "Ok. $sourceTableName equals $targetTableName"
    }

    private fun parseSubset(cobjs: List<String>, sourceTable: Table, database: Database, subj:String): TraceCmdResult {
        val targetTableName = cobjs[0]
        val keyCols = if (subj.contains(":")) subj.substringAfterLast(":").split(",")
        else sourceTable.columns.map { it.name }
        val elements = Column.set(keyCols)
        sourceTable.primaryKey = cobjs[0]
        val sourceTableName = sourceTable.name.substringBefore(":")
        val relation = database.schema.relation(sourceTableName)
        val tableName = relation?.name ?: sourceTableName
        database.schema.addSubset(tableName, targetTableName, elements)
        return TraceCmdResult() message "Ok. ${sourceTable.name} subset $targetTableName"
    }

    private fun parseExcludes(cobjs: List<String>, sourceTable: Table, database: Database, subj:String): TraceCmdResult {
        val targetTableName = cobjs[0]

        val keyCols = if (subj.contains(":")) subj.substringAfterLast(":").split(",")
        else sourceTable.columns.map { it.name }
        val elements = Column.set(keyCols)
        sourceTable.primaryKey = cobjs[0]
        val sourceTableName = sourceTable.name.substringBefore(":")
        val relation = database.schema.relation(sourceTableName)
        val tableName = relation?.name ?: sourceTableName
        database.schema.addDisjoint(tableName, targetTableName, elements)

        return TraceCmdResult() message "Ok. ${sourceTable.name} excludes ${targetTableName}"
    }

    private fun parseAs(cobjs: List<String>, subjectTable: Table): TraceCmdResult {
        if (cobjs.isEmpty()) return TraceCmdResult() failure "Required the alias name as argument."
        subjectTable.view = cobjs[0]
        return TraceCmdResult() message "Ok. ${subjectTable.name} as ${subjectTable.view}"
    }

    enum class TripletRole {SUBJECT, PREDICATE, COBJECT, COBJECTS, SUBJECT_PREFIX, COBJECT_PREFIX }
    data class Triplet(val subj:String, val pred:String, val cobj:String?)

    internal fun parseAsTripletMap(command:String):Map<TripletRole,String>? {
        val result = mutableMapOf<TripletRole,String>()
        val tokens = command.trim().split(" ")
        if (tokens.size <= 2) return null
        if (tokens.size == 3) { // add-fact person smokes
            result[TripletRole.SUBJECT] = tokens[1] // person
            result[TripletRole.PREDICATE] = "has"
            result[TripletRole.COBJECT] = tokens[2] // smokes
        } else {
            var parseStep = 1// tokens[0] = "add-fact"
            val cardinalitySubj = checkTokenCardinality(tokens[parseStep])
            if (cardinalitySubj!=null) {
                result[TripletRole.SUBJECT_PREFIX] = cardinalitySubj
                parseStep++
            }
            result[TripletRole.SUBJECT] = tokens[parseStep++]
            result[TripletRole.PREDICATE] = tokens[parseStep++]
            val cardinalityCobj = checkTokenCardinality(tokens[parseStep])
            //if (tokens[0].first().isLowerCase()) {
            if (cardinalityCobj!=null) {
                result[TripletRole.COBJECT_PREFIX] = cardinalityCobj
                parseStep++
            }
            result[TripletRole.COBJECT] = tokens.drop(parseStep).joinToString(" ")
            if (tokens[parseStep].startsWith("\"")) {
                result[TripletRole.COBJECT] = ""
                for (i in parseStep..tokens.size) {
                    result[TripletRole.COBJECT] = result[TripletRole.COBJECT]?.trim() + " " + tokens[i]
                    if (tokens[i].endsWith("\"")) break
                }
            }
        }
        println(result)
        return result
    }

    internal fun parseTriplet(command:String):Triplet {
        val tokens = command.split(" ")
        return Triplet(tokens[1],tokens[2],tokens.getOrNull(3))
    }

    internal fun parseNewTable(subj: String, database: Database, changeSet: ChangeSet): Table {
        val sourceTable1= Table()
        sourceTable1.name(subj)
        val colKey = Column(subj, "", "")
        sourceTable1.columns.add(colKey)
        database.schema.tables().add(sourceTable1)
        val keyConstraint = database.schema.addKey(subj, setOf(colKey))
        changeSet.createTable.addAll(listOf(sourceTable1))
        changeSet.createConstraint.addAll(listOf(keyConstraint))
        return sourceTable1
    }

    internal fun parseRelationship(
        database: Database,
        subj: String,
        pred: String,
        cobj: String?,
        subjPrefix: String? = null,
        cobjPrefix: String? = null
    ): TraceCmdResult {
        if (cobj == null) return TraceCmdResult() failure "Column names are required as argument."
        var targetTable: Table? = database.schema.table(cobj)
        if (targetTable == null) {
            // create a new target table
            targetTable = Table()
            targetTable.name(cobj)
            val colKey = Column(cobj, "", "")
            targetTable.columns.add(colKey)
            database.schema.tables().add(targetTable)
            // database.schema.table(subj)?.withCols(setOf(colKey))
// TO FIX            database.schema.addForeignKey(subj, setOf(colKey))
        }
        val relName = safeName("${subj}_${pred}_${cobj}")
        //val displayName = safeName(relName)
        var relationTable: Table? = database.schema.relation(subj)
        if (relationTable == null) {
            // create a new (binary) relation table
            relationTable = Table()
            relationTable.addLabels("relation")
            relationTable.name(relName)

            val keyId = pred.dashToCamelCase()
            val colKey = Column(keyId, "", "")
            relationTable.columns.add(colKey)

            val sourceId = subj
            val colSource = Column(sourceId, "", "")
            relationTable.columns.add(colSource)
            if (subjPrefix!=null) colSource.cardinality = subjPrefix
            if (subjPrefix=="1") {
                val sourceTable = database.schema.table(subj)
                sourceTable?.addNotExistentCols(Column.set(pred))
            }

            val targetId = cobj
            val colTarget = Column(targetId, "", "")
            colTarget.role = pred
            relationTable.columns.add(colTarget)
            if (cobjPrefix!=null) colTarget.cardinality = cobjPrefix
            if (cobjPrefix=="1") {
                targetTable.addNotExistentCols(Column.set(pred))
            }

            database.schema.tables().add(relationTable)
            database.schema.addKey(relName, setOf(colKey))

            val sourceConstraint = "$subj:$sourceId-->$relName:$sourceId"
            database.schema.addForeignKey(sourceConstraint)

            val targetConstraint = "$cobj:$targetId-->$relName:$targetId"
            database.schema.addForeignKey(targetConstraint)

        } else {
            // appends to existing relation a foreign key to target (turns ternary, quaternary,...)
            val targetId = cobj
            val colKey = Column(targetId, "", "")
            colKey.role = pred
            relationTable.columns.add(colKey)
            relationTable.addLabels("relation")

            val targetConstraint = "$cobj:$targetId-->$relName:$targetId"
            database.schema.addForeignKey(targetConstraint)
        }
        return TraceCmdResult() message "Relation added."
    }

    /*
    internal fun parseUnaryRelationship(database: Database, subj: String, pred: String): TraceCmdResult {
        //val subjectTable = parseNewTable(subj, database, changeSet)
        //parseHas(pred,subj,)
        return TraceCmdResult() message "Relation added."
    }
     */

    private fun safeName(originalName: String): String {
        return originalName.replace(":","_")
    }

    internal fun parseIsExclusively(cobjs: List<String>, database: Database, subj: String): TraceCmdResult {
        database.schema.addIsA(subj,cobjs[0].split(","),SchemaIsA.EXCLUSIVE.name)
        return TraceCmdResult() message "Exclusive constraint added."
    }

    internal fun parseIsPartitioned(cobjs: List<String>, database: Database, subj: String): TraceCmdResult {
        database.schema.addIsA(subj,cobjs[0].split(","),SchemaIsA.PARTITION.name)
        return TraceCmdResult() message "Partition added."
    }

    internal fun parseIsCovered(cobjs: List<String>, database: Database, subj: String): TraceCmdResult {
        database.schema.addIsA(subj,cobjs[0].split(","),SchemaIsA.COVER.name)
        return TraceCmdResult() message "Covering added."
    }

    // add-fact Person is-a
    internal fun parseIsA(cobjs: List<String>, database: Database, subj: String): TraceCmdResult {
        val parentNames = cobjs[0].split(",")
        val condition = cobjs.drop(1).joinToString(" ")
        for (parentName in parentNames) {
            database.schema.addIsA(parentName, listOf(subj), condition)
        }
        database.schema.table(subj)?.parent = cobjs[0]
        return TraceCmdResult() message "Parent added."
    }

    internal fun parseHas(cobjs: List<String>, database: Database, sourceTable: Table): TraceCmdResult {
        if (cobjs.isEmpty()) return TraceCmdResult() failure "Colum names are required as argument."
        val cobj = cobjs[0]
        if (cobj.contains(",")) {
            parseMultiColumnConstraint(cobjs,database,sourceTable)
            return TraceCmdResult() message  "Colum $cobj constraint added."
        }
        if (sourceTable.hasColumn(cobj)) {
            val col = sourceTable.colByName(cobj) ?: return TraceCmdResult() failure "Column name error."
            injectColAttributes(col, cobjs)
            return TraceCmdResult() message "Colum reference $cobj updated."
        } else {
            val col = Column(cobj, "_c"+sourceTable.columns.size+1, "")
            injectColAttributes(col, cobjs)
            sourceTable.columns.add(col)
//            val changeTable = Table()
//            changeTable.name = sourceTable.name
//            changeTable.columns.add(col)
//            val createColumns = ArrayList<CreateColumn>()
//            createColumns.add(changeTable)
//            changeSet.createColumn = createColumns
            return TraceCmdResult() message  "Colum $cobj added."
        }
    }

    private fun parseMultiColumnConstraint(
        cobjs: List<String>,
        database: Database,
        sourceTable: Table,
    ) {
        for (cobjx in cobjs.drop(1)) {
            if (cobjx == "at-most-one") {
                val targetColumnNames = cobjs[0].split(",")
                val colConstraint = targetColumnNames.joinToString(",")
                database.schema.addUnique("${sourceTable.name}:${colConstraint}")
            } else if (cobjx == "or-at-least-one") {
                val targetRelationNames = cobjs[0].split(",")
                val sourceName = targetRelationNames[0]
                val targetName = targetRelationNames[1]
                val constrainedCol = sourceTable.name
                database.schema.addOrAtLeastOne("${sourceName}:${constrainedCol}-->${targetName}:${constrainedCol}")
            }
        }
    }

    val dbPrefixes = listOf("dbtype:","h2:")// sources types
    val typePrefixes = listOf("type:","schema:","rdf:","rdfs:") // vocabularies types
    val cardinalityPrefixes = listOf("1..","0..","N..")
    val rolePrefixes = "as:"
    val defaultPrefixes = "may-be:"
    private fun injectColAttributes(col: Column, cobjs: List<String>) {
        for (cobjx in cobjs.drop(1)) {
            for (prefix in dbPrefixes) if (cobjx.startsWith(prefix)) col.dbtype = cobjx
            for (prefix in typePrefixes) if (cobjx.startsWith(prefix)) col.type = cobjx
            for (prefix in cardinalityPrefixes) if (cobjx.startsWith(prefix)) col.cardinality = cobjx
            for (prefix in rolePrefixes) if (cobjx.startsWith(prefix)) col.role = cobjx.substringAfterLast(":")
            for (prefix in defaultPrefixes) if (cobjx.startsWith(prefix)) col.default = cobjx.substringAfterLast(":")
            col.cardinality = checkTokenCardinality(cobjx) ?: col.cardinality
        }
    }

    private fun checkTokenCardinality(token: String):String? {
        if (token == "some")  return  "0..N"
        if (token == "at-least-one")  return "1..N"
        if (token == "at-most-one") return "0..1"
        if (token == "exactly-one") return "1"
        if (token == "no-cardinality") return ""
        if (token.contains("..")) return token
        return null
    }

    internal fun parseHasKey(cobjs: List<String>, sourceTable: Table, database: Database, subj: String): TraceCmdResult {
        val keyCols = cobjs[0].split(",")
        val elements = Column.set(keyCols)
        //sourceTable.columns.addAll(elements)
        sourceTable.primaryKey = cobjs[0]
        database.schema.addKey(subj, elements)
        return TraceCmdResult() message "Identify scheme added."
    }

    internal fun parseHasId(cobjs: List<String>, sourceTable: Table, database: Database, subj: String): TraceCmdResult {
        val keyCols = cobjs[0].split(",")
        val elements = Column.set(keyCols)
        sourceTable.addNotExistentCols(elements)
        //sourceTable.columns.addAll(elements)
        val jointKey = cobjs[0]
        sourceTable.naturalKey = jointKey
        if (sourceTable.primaryKey == null) sourceTable.naturalKey = jointKey
        database.schema.addId(subj, elements)
        return TraceCmdResult() message "Identify scheme added."
    }

    internal fun parseLabels(cobjs: List<String>, subjectTable: Table): TraceCmdResult {
        subjectTable.resetLabels(cobjs.joinToString(","))
        return TraceCmdResult() message "Labels added."
    }
}
