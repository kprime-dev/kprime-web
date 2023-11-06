package it.unibz.krdb.kprime.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.trace.TraceName
import org.openprovenance.prov.core.jsonld11.serialization.ProvSerialiser
import org.openprovenance.prov.model.Namespace
import org.openprovenance.prov.model.QualifiedName
import org.openprovenance.prov.vanilla.ProvFactory
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import unibz.cs.semint.kprime.usecase.common.MetaSchemaReadUseCase
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import it.unibz.krdb.kprime.adapter.jackson.JacksonFlattener
import it.unibz.krdb.kprime.adapter.jackson.JsonTable
import it.unibz.krdb.kprime.domain.trace.TraceFileName
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

// TODO Switch to TraceName usage instead of String.
class DataServiceAdapter(
    val settingService: SettingService,
    val sourceService: SourceService,
    val rdfService: RdfService) : DataService {

    override fun getTable(database: Database, entityName: String): Result<Table> {
        val table: Table = database.schema.table(entityName)
            ?: database.mappingAsTable(entityName)
            ?: return Result.failure(IllegalArgumentException("table $entityName not found"))
        return Result.success(table)
    }

    data class DatabaseInfo(val lastUpdate:String,val nextDb:String, val prevDb:String)

    override fun getDatabaseInfo(projectLocation: PrjContextLocation,
                                 traceName: String,
                                 traceFileName: String
                                ): DatabaseInfo {
        val traceFileTimeLastUpdate = getTraceFileTimeLastUpdate(traceName, traceFileName + "_db.xml", projectLocation)
            .map { it.toString() }.getOrDefault("LastUpdateTime-unknown")
        val orderedFileNames  = lastupdateOrderedtraceFileNames(traceName, projectLocation)
        val prevFileName = prevFileName(orderedFileNames,traceFileName)
        val nextFileName = nextFileName(orderedFileNames,traceFileName)
        return DatabaseInfo(traceFileTimeLastUpdate,nextFileName, prevFileName)
    }

    private fun lastupdateOrderedtraceFileNames(traceName: String, projectLocation: PrjContextLocation): List<String> {
        val workingTraceDir = projectLocation.value + SettingService.TRACES_DIR + traceName + "/"
        println("lastupdateOrderedtraceFileNames:$workingTraceDir")
        return File(workingTraceDir)
            .listFiles{ f -> f.isFile && f.name.endsWith("_db.xml") }.orEmpty()
            .sortedBy { it.lastModified() }
            .mapNotNull { f->  f.nameWithoutExtension.dropLast(3) }
        //return listOf("alfa","base","beta")
    }

    private fun prevFileName(orderedFileNames: List<String>, traceFileName: String): String {
        for (i in 1..orderedFileNames.size-1) {
            if (orderedFileNames[i]==traceFileName) return orderedFileNames[i-1]
        }
        return ""
    }

    private fun nextFileName(orderedFileNames: List<String>, traceFileName: String): String {
        for (i in 0..orderedFileNames.size-2) {
            if (orderedFileNames[i]==traceFileName) return orderedFileNames[i+1]
        }
        return ""
    }

    override fun getDatabase(
        projectLocation: PrjContextLocation,
        traceName: String,
        traceFileName: String
    ): Result<Database> {
        println("DataServiceAdapter:getDatabase projectLocation $projectLocation traceName $traceName traceFileName $traceFileName")
        return getTraceFileContent(traceName, traceFileName + "_db.xml", projectLocation)
            .map {
                //println("DataServiceAdapter:$it");
                XMLSerializerJacksonAdapter().deserializeDatabase(it) }
            .map {
                it.time =
                    getTraceFileTimeLastUpdate(traceName, traceFileName + "_db.xml", projectLocation).getOrNull().toString()
                println("DataServiceAdapter: map last updated")
                it
            }.onFailure {
                println(it.stackTrace)
            }
        //.recover { newBase() }
    }

    override fun writeDatabase(
        database: Database,
        projectLocation: PrjContextLocation,
        traceName: TraceName
    ) {
        val traceNameDir = if(traceName.value.endsWith("/"))
            traceName.value
        else traceName.value + "/"
        val databaseTraceName = projectLocation.value + traceNameDir
        val databaseName = database.name.ifEmpty { "base" }
        if (!File(databaseTraceName).isDirectory)  File(databaseTraceName).mkdirs()
        val databaseContent = prettyDatabase(database)
        val traceFileName = databaseTraceName + databaseName + "_db.xml"
        val tracePrevFileName =  databaseTraceName + databaseName + "_prev_db.xml"
        val traceFile = File(traceFileName)
        if (traceFile.exists())
            traceFile.copyTo(File(tracePrevFileName),overwrite = true)
        traceFile.writeText(databaseContent)
    }

    private fun getTraceFileContent(traceName: String,traceFileName: String, projectLocation: PrjContextLocation): Result<String> {
        var projectDir = projectLocation.value
        if (projectDir.isEmpty()) projectDir = settingService.getWorkingDir()
        val workingTraceFileName = projectDir + SettingService.TRACES_DIR + traceName + "/" + traceFileName
        return kotlin.runCatching { File(workingTraceFileName).readText(Charsets.UTF_8) }
    }

    private fun getTraceFileTimeLastUpdate(traceName: String,traceFileName: String, projectLocation: PrjContextLocation): Result<Date> {
        var projectDir = projectLocation.value
        if (projectDir.isEmpty()) projectDir = settingService.getWorkingDir()
        val workingTraceFileName = projectDir + SettingService.TRACES_DIR + traceName + "/" + traceFileName
//        println("----------------TraceInfo:")
//        println(workingTraceFileName)
//        println(Date(File(workingTraceFileName).lastModified()))
//        println("----------------TraceInfo END")
        return kotlin.runCatching { Date(File(workingTraceFileName).lastModified()) }

    }

    fun putTraceFile(
        projectLocation: String,
        traceName: String,
        traceFileName: String,
        fileContent: String) {
        if (!File(projectLocation + traceName).exists()) File(projectLocation + traceName).mkdir()
        val workingTraceFileName = "$projectLocation$traceName/$traceFileName"
        File(workingTraceFileName).writeText(fileContent,Charsets.UTF_8)
    }

    override fun addChangeSet(traceName: String, traceFileName: String, changeSet: ChangeSet, author: String) {
        val traceDir = this.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + traceName
        if (!File(traceDir).exists()) File(traceDir).mkdir()
        changeSet.author = author
        changeSet.id = UUID.randomUUID().toString()
        changeSet.time = (LocalDateTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val sourceContent = XMLSerializerJacksonAdapter().prettyChangeSet(changeSet)
        val revision = LocalDateTime.now().nano
        val csName = traceFileName + "_${revision}_cs.xml"
        val sourceTraceFileName = "$traceDir/$csName"
        File(sourceTraceFileName).writeText(sourceContent)
    }

    override fun addDatabase(traceName: String, traceFileName: String, database: Database, author: String) {
        val traceDir = this.settingService.getWorkingDir() + SettingService.TRACES_DIR + "/" + traceName
        if (!File(traceDir).exists()) File(traceDir).mkdir()
        database.author = author
        database.id = UUID.randomUUID().toString()
        database.time = (LocalDateTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val sourceContent = XMLSerializerJacksonAdapter().prettyDatabase(database)
        val revision = LocalDateTime.now().nano
        val csName = traceFileName + "_${revision}_db.xml"
        val sourceTraceFileName = "$traceDir/$csName"
        File(sourceTraceFileName).writeText(sourceContent)
    }

    override fun getChangeSets(traceName: String): List<ChangeSet> {
        val workingTraceDir = settingService.getTraceDir(TraceName(traceName))
        val serializer = XMLSerializerJacksonAdapter()
        return File(workingTraceDir)
            .listFiles{ f -> f.isFile && f.name.endsWith("_cs.xml") }.orEmpty()
            .mapNotNull { f->  f.readText() }
            .map { text -> serializer.deserializeChangeSet(text)}
    }

    override fun getDatabases(traceName: String): List<Database> {
        val workingTraceDir = settingService.getTraceDir(TraceName(traceName))
        val serializer = XMLSerializerJacksonAdapter()
        return File(workingTraceDir)
            .listFiles{ f -> f.isFile && f.name.endsWith("_db.xml") }.orEmpty()
            .mapNotNull { f->  f.readText() }
            .map { text -> serializer.deserializeDatabase(text)}
    }

    /**
     * conditions = "key1=val1&key2=val2"
     */
    override fun queryJsonEntity(prjContext: PrjContext,table: Table, database: Database, conditions: String?): String {
        //if (database.source.isEmpty()) return "no-datasource"
        val sourceToQuery = database.source
        val datasource = if (sourceToQuery.isNotEmpty()) extractDataSource(prjContext,sourceToQuery)?: h2DataSource(sourceToQuery)
            else h2DataSource()
        val select = computeSelectString(table, database, conditions)
        val adapter = JdbcAdapter(false)
        println("queryJsonEntity.select:$select")
        val resultSet = adapter.query(datasource, select)
        val referencedTables = database.schema.referencedTablesOf(table.name)
//        for (refTable in referencedTables) {
            /**
             * select p.id,depname,depaddress
             * from emp-dep
             * left join department as d
             * on d.depname=emp-dep.depname
             *
             * select depid
             * from emp-dep
             * where emp.id=""
             *
             * select depname,depaddress
             * from department
             * where depname=depid
             */
            //val referencedSet = adapter.query(datasource, SQLizeSelectUseCase().sqlize(Query.Companion.buildFromTable(database,table.name,database.schema.k)))
            //val entity = JdbcPrinter.printJsonResultList(resultSet,referencedSet)

//        }
        val externalSourceConstraints = database.schema.foreignsWithSource(table.name)
        val externalKeys = externalSourceConstraints.map { it.source.columns[0].name to "http://localhost:7000/project/kprime-case-confucius-mysql/data/root/base/${it.target.table}?${it.target.columns[0].name}=" }.toMap()
        val externalTargetConstraints = database.schema.foreignsWithTarget(table.name)
        val externalSources = externalTargetConstraints.map { "ext-"+it.source.columns[0].name to "http://localhost:7000/project/kprime-case-confucius-mysql/data/root/base/${it.source.table}?${it.source.columns[0].name}=${it.target.columns[0].name}" }.toMap()
        println("-------------------externalKeys:")
        println(externalKeys)
        println("-------------------externalKeys END")
        val header = mapOf(
            "kp_version" to "1.0",
            "generated_on" to LocalDateTime.now().toString(),
            "openapi" to "http://localhost:7777/context/${prjContext.name}/dictionary/traceName/traceFileName/jsonapi",
            "size" to resultSet.size
        )
        val entity = JdbcPrinter.printJsonResultList(header,resultSet,mapOf("works-in" to "http://www.google.com",
                "has-phone" to listOf("348","567"),
                ), externalKeys, externalSources
        )

        println("queryJsonEntity.entity:$entity")
        val entityRefs=""
        // TODO follow foreign links to unfold sql query
//        var foreigns = computeForeigns(database, table)
//        for (foreign in foreigns) {
//            var selectForeign = computeForeignSelect(foreign)
//            entityRefs = adapter.query(datasource, selectForeign, adapter::printJsonResultSet)
//
//        }
        return "$entity $entityRefs"
    }

    private fun h2DataSource(sourceToQuery:String = "mem_db"): DataSource {
        return DataSource("h2", sourceToQuery, "org.h2.Driver", "jdbc:h2:mem:$sourceToQuery", "sa", "")
    }

    private fun computeForeigns(database: Database, table: Table):List<Constraint> {
        val fkeys = database.schema.foreignKeys()//.filter { f -> f.source.table.equals(table.name) }
        val fkeysTable = mutableListOf<Constraint>()
        for (fkey in fkeys) {
            if (fkey.source.name == table.name)
                fkeysTable.add(fkey)
        }
        return fkeysTable
    }

    private fun computeForeignSelect(fkey:Constraint): String {
        var selectForeign = "SELECT * FROM "
        selectForeign += fkey.target.name+" WHERE " +
                fkey.target.name + "." + fkey.target.columns[0] + "=" +
                fkey.source.name + "." + fkey.source.columns[0]
        return selectForeign
    }

    override fun extractDataSource(prjContext: PrjContext, sourceName: String): DataSource? {
        val existingDataSource = sourceService.newWorkingDataSource(prjContext, sourceName)
        println("extractDataSource $existingDataSource sourceName, $sourceName prjContext $prjContext")
        if (existingDataSource!=null && existingDataSource.type=="CSV") {
            return newDataSourceFromCSV(existingDataSource, PrjContextLocation(prjContext.location))
        }
        return existingDataSource
    }

    override fun newDataSourceFromCSV(
        existingDataSource: DataSource,
        prjContextLocation: PrjContextLocation
    ): DataSource {
        val csvFilePath = existingDataSource.path.substringAfterLast(":")
        val workingDataDir = prjContextLocation.value + TraceFileName(csvFilePath).getTraceName().toDirName()
        println("TraceCmdCreateTableFromCsv.workingDataDir: [$workingDataDir]")
        val fileName = TraceFileName(csvFilePath).getFileName()
        println("TraceCmdCreateTableFromCsv.fileName: [$fileName]")
        val h2MemDataSource = DataSource(
            "h2",
            existingDataSource.name,
            "org.h2.Driver",
            "jdbc:h2:mem:${existingDataSource.name}",
            "sa",
            ""
        )
        h2MemDataSource.connection =
            DataSourceConnection(existingDataSource.name, "sa", "", autocommit = true, commited = true, closed = false)
        createTableFromFileCsv(
            workingDataDir = workingDataDir,
            filename = fileName,
            datasource = h2MemDataSource,
            tableName = existingDataSource.name
        )
        return h2MemDataSource
    }

    override fun computeSelectString(table: Table, database: Database, conditions: String?): String {
        val cols = table.columns.joinToString(",") { c -> c.name }
//        val condition = database.schema.key(table.name)
//        val fkeys = database.schema.foreignKeys().filter { f -> f.source.table==table.name }

        val askedTable = table.name
        // nested select of mappings
        val mapping = database.mapping(askedTable)
        if (mapping !=null) {
            var conditionStr  = ""
            if (!conditions.isNullOrEmpty()) {
                conditionStr += " " + conditions.replace("&", " AND ")
                if (mapping.select.where.condition.isNotEmpty()) {
                    mapping.select.where.condition += " AND $conditionStr"
                } else {
                    mapping.select.where.condition += conditionStr
                }
            }
            return SQLizeSelectUseCase().sqlize(mapping) + " LIMIT 10"
        }

        // TODO conditions check
        var select = "SELECT $cols FROM $askedTable "

        if (!conditions.isNullOrEmpty()) {
            select += " WHERE " + conditions.replace("&", " AND ")
        }
        select += " LIMIT 10"
        return select
    }

    override fun queryLinkedEntity(
        prjContext: PrjContext,
        table: Table,
        database: Database,
        conditions: String?
    ): String {
        if (database.source.isEmpty()) return ""
        val sourceName = database.source
        val datasource = extractDataSource(prjContext, sourceName) ?: return "datasource-not-found [$sourceName]"
        val select = computeSelectString(table, database, conditions)
        val adapter = JdbcAdapter(false)
        val iriContext = "http://kp.queryLinkedEntity/"
        val rdfDataDir = RdfService.getPrjContextRdfDataDir(PrjContextLocation(prjContext.location))
        return adapter.query(datasource, select, RdfJdbcPrinter(iriContext,rdfService, database, table, rdfDataDir)::printJsonLDResultSet)
    }

    private fun qn (pf: ProvFactory, ns:Namespace, name: String): QualifiedName {
        return pf.newQualifiedName(ns.defaultNamespace, name, "pc1")
    }

    // TODO WIP use real values, not mocks!
    override fun queryJsonProvenance(table: Table, database: Database, conditions: String?): String {
        if (database.source.isEmpty()) return ""
        val ns = Namespace()
        ns.addKnownNamespaces()
        val pFactory = ProvFactory()
        val quote = pFactory.newEntity(qn(pFactory,ns, "a-little-provenance-goes-a-long-way"))
        quote.value = pFactory.newValue("A little provenance goes a long way", pFactory.getName().XSD_STRING)

        val doc = pFactory.newDocument()

        val provSerializer = ProvSerialiser()
        val outStream = ByteArrayOutputStream()
        provSerializer.serialiseDocument(outStream,doc,true)
//        iFramework.writeDocument(outStream, Formats.ProvFormat.JSONLD,doc)
        return(outStream.toString(Charsets.UTF_8.name()))

    }

    override fun newBase(): Database {
        println("DataServiceAdapter:newBase")
        val db = Database().withGid()
        db.name = "base"
        db.id = db.gid?:""
        println("db.id [_${db.id}_]")
        return db
    }

    override fun prettyDatabase(metaDatabase: Database): String {
        return XMLSerializerJacksonAdapter().prettyDatabase(metaDatabase)
    }

    override fun prettyChangeSet(changeSet: ChangeSet): String {
        return XMLSerializerJacksonAdapter().prettyChangeSet(changeSet)
    }

    override fun createBaseProjectFile(setting: Setting) {
        val workingDir = setting.workingDir
        File("$workingDir/.prime/traces/root").mkdirs()
        val db = Database().withGid()
        db.name = ""
        val emptyDbXml = XMLSerializerJacksonAdapter().prettyDatabase(db)
        File("$workingDir/.kprime/traces/root/base_db.xml").writeText(emptyDbXml)
    }

    override fun databaseFromDatasource(datasource: DataSource): Database {
        var database: Database = newBase()
        when (datasource.type) {
            "psql" -> {
                val caseResult = MetaSchemaReadUseCase().doit(
                    datasource,
                    "read-meta-schema sakila-source",
                    MetaSchemaJdbcAdapter(),
                    XMLSerializerJacksonAdapter())
                if (caseResult.ok != null) {
                    database = caseResult.ok as Database
                }
            }
            "h2" -> {
                val caseResult = MetaSchemaReadUseCase().doit(
                    datasource,
                    "read-meta-schema sakila-source",
                    MetaSchemaJdbcAdapter(),
                    XMLSerializerJacksonAdapter())
                if (caseResult.ok != null)
                    database = caseResult.ok as Database
            }
            "xml" -> {
                val file = File(datasource.path)
                database = if (file.exists()) {
                    val dbXml = file.readText(Charsets.UTF_8)
                    XMLSerializerJacksonAdapter().deserializeDatabase(dbXml)
                } else {
                    Database().withGid()
                }

            }
        }
        return database
    }

    override fun writeDatabaseFileFromDataSource(
        tracesDir: String,
        sourceName: String,
        datasource: DataSource
    ): String {
        if (!File(tracesDir).isDirectory)
            File(tracesDir).mkdir()
        val nextSeed = Date().time
        val dbName = sourceName + nextSeed
        val workingTraceDirName = tracesDir + dbName
        File(workingTraceDirName).mkdir()
        val dbnameFileName = dbName + "_db.xml"
        val sourceTraceFileName = "$workingTraceDirName/$dbnameFileName"
        val database = databaseFromDatasource(datasource)
        database.name = dbnameFileName
        val sourceContent: String = prettyDatabase(database)
        File(sourceTraceFileName).writeText(sourceContent)
        return dbName
    }

    override fun databaseFromSourceOrNewH2(toMetaSourceName: String): Database {
        val workingSource= sourceService.newWorkingDataSourceOrH2(toMetaSourceName)
        return MetaSchemaJdbcAdapter().metaDatabase(workingSource,Database().withGid(),"",null,null)
    }

    override fun databaseFromXml(xml: String):Database {
        return XMLSerializerJacksonAdapter().deserializeDatabase(xml)
    }

    override fun changeSetFromXml(xml: String):ChangeSet {
        return XMLSerializerJacksonAdapter().deserializeChangeSet(xml)
    }

    override fun query(workingDataSource: DataSource, sqlCommand:String): String {
        return JdbcAdapter().query(workingDataSource, sqlCommand, JdbcPrinter::printResultSet)
    }

    override fun queryFunctionals(workingDataSource: DataSource, database: Database, tableName :String): String {
        var selectCounter = 0
        val adapter = JdbcAdapter()
        val tables = if (tableName.isEmpty()) database.schema.tables()
        else {
            val tab = database.schema.table(tableName)
            if (tab==null) emptyList()
            else listOf(tab)
        }
        val functionalsFound = mutableListOf<String>()
        println("Search functionals on ${tables.size} tables:")
        println(tables.joinToString { it.name })
        for (table in tables) {
            val cols = table.columns
            println(cols.joinToString { it.name })
            for (col1 in cols) {
                if (database.schema.keyPrimary(tableName)?.source?.columns?.any { it.name == col1.name } == true) continue
                for (col2 in cols) {
                    if (col1 != col2) {
                        selectCounter++
                        val sqlQuery = " SELECT t1.$col1, t1.$col2 FROM ${table.name} t1, ${table.name} t2  WHERE t1.$col1 = t2.$col1 and  t1.$col2 <> t2.$col2 "
                        println(sqlQuery)
                        val queryResult = adapter.query(workingDataSource, sqlQuery, JdbcPrinter::printResultSet)
                        println("------")
                        println(queryResult.length)
                        if (queryResult.length==0) {
                            functionalsFound.add("t1.$col1 --> $col2")
                        }
                        //println(queryResult)
                    }
                }
            }
        }
        val functionalsResult = "${functionalsFound.size} functionals found on $selectCounter select.\n"+functionalsFound.joinToString("\n")
        return functionalsResult
    }

    override fun createSqlFromFileJson(
        workingDataDir: String,
        filename: String,
        datasource: DataSource
    ):Result<String> {
        val jsonString = File(workingDataDir + filename).readText(Charsets.UTF_8)
        val createTable = fromJsonToSql(jsonString,filename)
        return Result.success(createTable.joinToString(System.lineSeparator()))
    }

    override fun createTableFromFileJson(
        workingDataDir: String,
        filename: String,
        datasource: DataSource
    ):Result<String> {
        val jsonString = File(workingDataDir + filename).readText(Charsets.UTF_8)
        val createTable = fromJsonToTable(jsonString,filename)
        return Result.success(createTable.joinToString(System.lineSeparator()))
    }


    companion object {

        internal fun fromJsonToSql(jsonString: String, filename: String): List<String> {
            val jsonNode = ObjectMapper().readTree(jsonString)
            val tables = mutableMapOf<String, JsonTable>()
            val table0 = mutableMapOf<String, Any>()
            JacksonFlattener.computeSchema(jsonNode, filename, 0, tables, table0)
            val resultString = tables.toString().replace(",", System.lineSeparator())
            println(">==============")
            println(resultString)
            println("<==============")
            val createTable = fromFlattenToSql(resultString)
            return createTable
        }

        internal fun fromJsonToTable(jsonString: String, filename: String): List<String> {
            val jsonNode = ObjectMapper().readTree(jsonString)
            val tables = mutableMapOf<String, JsonTable>()
            val table0 = mutableMapOf<String, Any>()
            JacksonFlattener.computeSchema(jsonNode, filename, 0, tables, table0)
            val resultString = tables.toString().replace(",", System.lineSeparator())
            println(">==============")
            println(resultString)
            println("<==============")
            val createTable = fromFlattenToTable(resultString)
            return createTable
        }

        internal fun fromFlattenToTable(line: String):List<String> {
            return line.replace(System.lineSeparator(),",")
                .split("}")
                .filter { it.trim().isNotEmpty() }
                .map { toTableLine(it) }
        }

        internal fun toTableLine(line:String):String {
            println("line:[$line]")
            val prefix = line
                .drop(1).substringBefore("=").trim()
            println("prefix:[$prefix]")
            val tableName = sqlName(prefix)
            val args = line
                .substringAfterLast("{")
                .split(",")
                .filter { it.isNotEmpty() }
                .map { toTableColumn(prefix+".", it) }
            println(args)
            val sqlLine = "create-table $tableName: ${args.joinToString(",")}"
            return sqlLine
        }

        fun toTableColumn(prefix:String,colToken: String):String {
            if (colToken.contains("=Text")) {
                return sqlName(" TEXT:"+colToken.trim().substring(prefix.length).substringBefore("="))
            } else if (colToken.contains("=Double")) {
                return sqlName(" NUMERIC:"+colToken.trim().substring(prefix.length).substringBefore("="))
            } else if (colToken.contains("=Boolean")) {
                return sqlName(" BOOLEAN:"+colToken.trim().substring(prefix.length).substringBefore("="))
            }
            return "-"
        }


        internal fun fromFlattenToSql(line: String):List<String> {
            return line.replace(System.lineSeparator(),",")
                .split("}")
                .filter { it.trim().isNotEmpty() }
                .map { toSqlLine(it) }
        }

        internal fun toSqlLine(line:String):String {
            println("line:[$line]")
            val prefix = line
                .drop(1).substringBefore("=").trim()
            println("prefix:[$prefix]")
            val tableName = sqlName(prefix)
            val args = line
                .substringAfterLast("{")
                .split(",")
                .filter { it.isNotEmpty() }
                .map { toSqlColumn(prefix+".", it) }
            println(args)
            val sqlLine = "CREATE TABLE $tableName (${args.joinToString(",")})"
            return sqlLine
        }

        fun toSqlColumn(prefix:String,colToken: String):String {
            if (colToken.contains("=Text")) {
                return sqlName(colToken.trim().substring(prefix.length).substringBefore("=")) + " VARCHAR(64)"
            } else if (colToken.contains("=Double")) {
                return sqlName(colToken.trim().substring(prefix.length).substringBefore("=")) + " NUMERIC"
            } else if (colToken.contains("=Boolean")) {
                return sqlName(colToken.trim().substring(prefix.length).substringBefore("=")) + " BOOLEAN"
            }
            return "-"
        }

        fun sqlName(rawString:String ):String {
            println(rawString)
            return rawString.replace('.','_').replace('-','_').lowercase()
        }
    }
}