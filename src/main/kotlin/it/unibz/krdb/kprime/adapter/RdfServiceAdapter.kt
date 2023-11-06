package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.term.LabelField
import it.unibz.krdb.kprime.domain.term.Term
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import it.unibz.krdb.kprime.support.noBlank
import it.unibz.krdb.kprime.support.substring
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Statement
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.util.ModelBuilder
import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.nativerdf.NativeStore
import unibz.cs.semint.kprime.adapter.repository.ResultList
import unibz.cs.semint.kprime.domain.db.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.net.URL

class RdfServiceAdapter(val settingService: SettingService): RdfService {

    /*
    val homeDir = settingService.getInstanceDir()
    val rdfDataPath = "$homeDir/rdf4j/"
    val rdfDataDir = File(rdfDataPath)
     */
    override fun import(iriContext:String, projectLocation: PrjContextLocation, inputFilePath:String, rdfDataDir:String):Result<String> {
        val inputUrl = if (inputFilePath.startsWith("http")) URL(inputFilePath) else null
        val inputFile = if (inputUrl==null) File(projectLocation.value+inputFilePath).inputStream() else null
        val rdfDataDirFile = File(rdfDataDir)
        if (!rdfDataDirFile.exists()) rdfDataDirFile.mkdir()
        val repo: Repository = SailRepository(NativeStore(rdfDataDirFile))
        try {
            repo.connection.use { conn ->
                val vf : ValueFactory = conn.valueFactory
                val context: IRI = vf.createIRI(iriContext)
                if (inputUrl!=null) {
                    println("inputUrl:[$inputUrl]")
                    conn.add(inputUrl, iriContext, RDFFormat.TURTLE, context)
                } else {
                    conn.add(inputFile, iriContext, RDFFormat.TURTLE, context)
                }
            }
        } catch (e: Exception ) {
            return Result.failure(e)
        } finally {
            repo.shutDown()
        }
        return Result.success("Labels from $inputFilePath added.")
    }

    override fun add(iriContext:String, subject:LabelField, predicate:LabelField, cobject:String, rdfDataDir:String): Result<String> {
        val model = ModelBuilder().namedGraph(iriContext)
            .subject(subject.toString()).add(predicate.toString(),cobject).build()

        val homeDir = settingService.getInstanceDir()
        val rdfDataDirUsed = File(rdfDataDir.ifEmpty { "$homeDir/rdf4j/" })
        if (!rdfDataDirUsed.exists()) rdfDataDirUsed.mkdir()
        println("RdfServiceAdapter.rdfDataDir:$rdfDataDir")
        val repo: Repository = SailRepository(NativeStore(rdfDataDirUsed))
        try {
            repo.connection.use { conn ->
                conn.add(model)
            }
        } catch (e: Exception ) {
            Result.failure<Exception>(e)
        } finally {
            repo.shutDown()
        }
        return Result.success("Label ($subject,$predicate,$cobject) added.")
    }

    override fun remove(iriContext: String, subject: LabelField, predicate: LabelField, cobject: String, rdfDataDir:String): Result<String> {
        val rdfDataDirUsed = File(rdfDataDir)
        if (!rdfDataDirUsed.exists()) rdfDataDirUsed.mkdir()
        println("RdfServiceAdapter.rdfDataDir:$rdfDataDir")
        val repo: Repository = SailRepository(NativeStore(rdfDataDirUsed))
        try {
            val findStatements = internalFindStatements(subject.toString(), predicate.toString(), cobject, rdfDataDir, iriContext)
                .getOrDefault(emptyList())
            repo.connection.use { conn ->
                conn.remove(findStatements)
            }
        } catch (e: Exception ) {
            Result.failure<Exception>(e)
        } finally {
            repo.shutDown()
        }
        return Result.success("Label ($subject,$predicate,$cobject) removed.")
    }

    override fun list(iriContext: String, rdfDataDir:String): Result<String> {
        val rdfDataPathUsed = rdfDataDir.ifEmpty {
            return Result.failure(IOException("rdfDataPath is required.")) }
        val resultMessage = listRdfStatements(rdfDataPathUsed)
        return Result.success("$resultMessage")
    }

    internal fun listRdfStatements(rdfDataDir: String): Serializable {
        val dataDir = File(rdfDataDir)
        if (!dataDir.exists()) dataDir.mkdir()
        val repo: Repository = SailRepository(NativeStore(dataDir))
        val resultMessage = try {
            repo.connection.use { conn ->
                conn.getStatements(null, null, null)
                    .joinToString { it.toString().replace(',', ' ') + System.lineSeparator() }
            }
        } catch (e: Exception) {
            Result.failure<Exception>(e)
        } finally {
            repo.shutDown()
        }
        return resultMessage
    }

    override fun removeNamespace(iriContext: String, rdfDataDir: String): Result<String> {
        val dataDir = File(rdfDataDir)
        if (!dataDir.exists()) dataDir.mkdir()
        val repo: Repository = SailRepository(NativeStore(dataDir))
        try {
            repo.connection.use { conn ->
                val vf : ValueFactory = conn.valueFactory
                val context = vf.createIRI(iriContext)
                conn.clear(context)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            repo.shutDown()
        }
        return Result.success("Label namespace [$iriContext] removed.")
    }

    override fun listStatements(): Result<List<RdfStatement>> {
        val homeDir = settingService.getInstanceDir()
        val dataDir = File("$homeDir/rdf4j/")
        val repo: Repository = SailRepository(NativeStore(dataDir))
        val rsult = kotlin.runCatching {
            println("rdfService.listStatements()")
            repo.connection.use { conn ->
                conn.getStatements(null, null, null)
                    .map { std -> println(std); RdfStatement.from4jStatement(std) }
            }
        }.onFailure {
            repo.shutDown()
            return Result.failure(it)
        }
        repo.shutDown()
        return rsult
    }

    internal fun internalFindStatements(subject: String, predicate: String, cobject: String,
                                        rdfDataDir:String, iriContext: String): Result<List<Statement>> {
        val dataDir = File(rdfDataDir)
        if (!dataDir.exists()) dataDir.mkdir()
        val repo: Repository = SailRepository(NativeStore(dataDir))
        val result : List<Statement>?
        try {
            result = repo.connection.use { conn ->
                val factory = SimpleValueFactory.getInstance()
                val findSubj = if (subject == "_") null else factory.createIRI(subject)
                val findPred = if (predicate == "_") null else factory.createIRI(predicate)
                val findCobj = if (cobject == "_") null else factory.createLiteral(cobject)
                if (iriContext.isNotEmpty()) {
                    val vf : ValueFactory = conn.valueFactory
                    val context = vf.createIRI(iriContext)
                    conn.getStatements(findSubj, findPred, findCobj, context).toList()
                } else {
                    conn.getStatements(findSubj, findPred, findCobj).toList()
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        } finally {
            repo.shutDown()
        }
        if (result==null) return Result.failure(IllegalArgumentException())
        return Result.success(result)
    }

    override fun findStatements(iriContext:String, subject: LabelField, predicate: LabelField, cobject: String, rdfDataDir: String): Result<List<RdfStatement>> {
        return internalFindStatements(subject.toString(),predicate.toString(),cobject,rdfDataDir,iriContext).fold(
            onSuccess = { Result.success(it.map { std -> RdfStatement.from4jStatement(std) }) },
            onFailure = { it.printStackTrace(); Result.failure(IllegalArgumentException()) }
        )
    }

    override fun find(iriContext: String, subject: LabelField, predicate: LabelField, cobject: String, rdfDataDir: String): Result<String> {
        lateinit var resultMessage :String
        internalFindStatements(subject.toString(),predicate.toString(),cobject,rdfDataDir=rdfDataDir,iriContext=iriContext).onSuccess { listOfStatements ->
            resultMessage = listOfStatements.joinToString { statement -> statement.toString().replace(',', ' ')+ System.lineSeparator() }
        }.onFailure {
            return Result.failure(IllegalArgumentException())
        }
        return Result.success(resultMessage)
    }

    override fun query(sparqlQuery:String, rdfDataDir:String): Result<String> {
        val dataDir =  File(rdfDataDir)
        if (!dataDir.exists()) dataDir.mkdir()
        val repo: Repository = SailRepository(NativeStore(dataDir))
        val result: String
        println("SPARQL QUERY=[$sparqlQuery]")
        val resultMessage = try {
            result = if(sparqlQuery.lowercase().contains("select"))
                sparqlSelect(repo, sparqlQuery)
            else
                sparqlConstruct(repo, sparqlQuery)
        } catch (e: Exception ) {
            return Result.failure(e)
        } finally {
            repo.shutDown()
        }
        return Result.success(" Result [$resultMessage] Bindings \n $result .")
    }

    override fun queryList(sparqlQuery:String, rdfDataDir:String): Result<ResultList> {
        val dataDir =  File(rdfDataDir)
        if (!dataDir.exists()) dataDir.mkdir()
        val repo: Repository = SailRepository(NativeStore(dataDir))
        val result = try {
                sparqlSelectList(repo, sparqlQuery)
        } catch (e: Exception ) {
            return Result.failure(e)
        } finally {
            repo.shutDown()
        }
        return Result.success(result)
    }

    private fun sparqlSelect(
        repo: Repository,
        sparqlQuery: String
    ): String {
        var result = ""
        repo.connection.use { conn ->
            val query = conn.prepareTupleQuery(sparqlQuery)
            val evaluate = query.evaluate()
            evaluate.use {
                while (it.hasNext()) {
                    val bindings = it.next()
                    for (bindingName in bindings.bindingNames) {
                        val value = bindings.getValue(bindingName)
                        result += ">>>> $bindingName = $value \n"
                    }
                }
            }
        }
        return result
    }

    private fun sparqlSelectList(
        repo: Repository,
        sparqlQuery: String
    ): ResultList {
        val result = mutableListOf<Map<String, Any>>()
        repo.connection.use { conn ->
            val query = conn.prepareTupleQuery(sparqlQuery)
            val evaluate = query.evaluate()
            evaluate.use {
                while (it.hasNext()) {
                    val bindings = it.next()
                    val row  = mutableMapOf<String, Any>()
                    for (bindingName in bindings.bindingNames) {
                        val value = bindings.getValue(bindingName)
                        row[bindingName] = value
                    }
                    result.add(row)
                }
            }
        }
        return result
    }

    private fun sparqlConstruct(
        repo: Repository,
        sparqlQuery: String
    ): String {
        val result = "SparQL constructed."
        repo.connection.use { conn ->
            val query = conn.prepareGraphQuery(sparqlQuery)
            val evaluate = query.evaluate()
            evaluate.use {result ->
                for (solution: Statement in result) {
                    solution.subject.toString()
                }
            }
        }
        return result
    }


    override fun toTurtle(
        projectName: String,
        vocabularies: List<Vocabulary>,
        terms: List<Term>,
        iriContext: String,
        prjContextLocation: PrjContextLocation
    ): String {
        val builder = ModelBuilder()

        // set some namespaces
        builder.setNamespace(projectName, "$iriContext$projectName#")
        //        builder.setNamespace(FOAF.NS)
        //        builder.setNamespace(OWL.NS)

        println("vocabularies: ${vocabularies.size}")
        for (vocabulary in vocabularies) {
            println("vocabulary: ${vocabulary.prefix.removeSuffix(":")} ${vocabulary.namespace}")
            builder.setNamespace(vocabulary.prefix.removeSuffix(":"), vocabulary.namespace)
        }


        builder.namedGraph(":graph1") // add a new named graph to the model
            .subject(":john") // add  several statements about resource ex:john

        //        builder.add(FOAF.NAME, "John") // add the triple (ex:john, foaf:name "John") to the named graph
        //            .add(FOAF.AGE, 42)
        //            .add(OWL.COMPLEMENTOF,"Jack")
        //            .add("gufo:alba","Xyz")
        //            .add(FOAF.MBOX, "john@example.org")


        for (term in terms) {
            builder.subject(":${term.name}") // add  several statements about resource ex:john
            if (term.type.isNotEmpty())
                builder.add("rdf:type", term.type) // add the triple (ex:john, foaf:name "John") to the named graph
            else
                builder.add("rdf:type", "owl:Class") // add the triple (ex:john, foaf:name "John") to the named graph
            if (term.description.isNotEmpty())
                builder.add("rdfs:comment", term.description)
            val rdfDataDir = RdfService.getPrjContextRdfDataDir(prjContextLocation)
            internalFindStatements(":"+term.name,"_","_",rdfDataDir, iriContext).onSuccess { listOfStatements ->
                listOfStatements.forEach{ statement -> builder.add(statement.predicate,statement.`object`) }
            }
        }
        builder.defaultGraph().add(":graph1", RDF.TYPE, ":Graph")
        val model = builder.build()
        val out = ByteArrayOutputStream()
        Rio.write(model, out, RDFFormat.TURTLE)
        return String(out.toByteArray())
    }

    override fun toDatabaseFromOntouml(iriContext:String, rdfDataDir:String, addLabels:Boolean):Database {
        val database = Database()

        /*
         CLASSES
         */

        val ontoumlClassNames = queryList("""
            PREFIX ontouml2: <https://purl.org/ontouml-models/vocabulary/> 
            PREFIX ontouml: <https://w3id.org/ontouml#> 
            SELECT ?name ?sbj ?stereotype
            {
             ?sbj ?pred ontouml:Class .
             ?sbj ontouml:name ?name . 
             ?sbj ontouml:stereotype ?stereotype 
            }
            """,rdfDataDir).getOrThrow()//Default(emptyList())
        println()
        println("----ontoumlClassNames---${ontoumlClassNames.size}--")

        for (entry in ontoumlClassNames) {
            val table = Table()
            table.name = entry["name"].toString().substring("\"","\"").noBlank()
            if (table.name.isEmpty()) continue
            table.id = entry["sbj"].toString().substringAfterLast("/").substringAfterLast("#")
            val stereotypeLabel = entry["stereotype"].toString().substringAfterLast("/").substringAfterLast("#")
            if (addLabels) {
                add(iriContext, LabelField(table.name), LabelField("ontouml:stereotype"), stereotypeLabel, rdfDataDir)
            }
//            table.addLabels(listOf(
//                (entry["stereotype"].toString()).substringAfterLast("/"),
//                (entry["sbj"].toString()).substringAfterLast("/"))
//            )
            database.schema.tables().add(table)
            table.columns.add(Column.of(table.name))
            println("----ontoumlClassNames---${table.name}-${table.id}-")
        }
        println(">>>>>>>>>>>>>>>>>>>>> DB size:"+database.schema.tables().size)

        /*
         RELATIONS
         */

        val ontoumlRelations = queryList("""
            PREFIX ontouml2: <https://purl.org/ontouml-models/vocabulary/> 
            PREFIX ontouml: <https://w3id.org/ontouml#>
            SELECT ?name ?sbj ?stereotype ?targetEnd ?sourceEnd 
            {
             ?sbj ?pred ontouml:Relation .
             ?sbj ontouml:name ?name . 
             ?sbj ontouml:stereotype ?stereotype .
             ?sbj ontouml:sourceEnd ?sourceEnd .
             ?sbj ontouml:targetEnd ?targetEnd .
            }
            """,rdfDataDir).getOrThrow()
        println()
        println("----ontoumlRelations---${ontoumlRelations.size}--")

        for (entry in ontoumlRelations) {
            println("entry:[$entry]")
            val relationTable = Table()
            relationTable.name = entry["name"].toString().substring("\"","\"")
            relationTable.id = entry["sbj"].toString().substringAfterLast("/")
            val stereotypeLabel = entry["stereotype"].toString().substringAfterLast("/").substringAfterLast("#")
            if (stereotypeLabel.isNotEmpty() && addLabels)
                add(iriContext, LabelField(relationTable.name), LabelField("ontouml:stereotype"), stereotypeLabel, rdfDataDir)

            println("----relation---${relationTable.name}-${relationTable.id}-")

            val sourceEnd = entry["sourceEnd"].toString().noBlank().substringAfterLast("#")
            println("---sourceEnd:[$sourceEnd]")
            val sparqlQuerySourceRole = """
            PREFIX ontouml2: <https://purl.org/ontouml-models/vocabulary/> 
            PREFIX ontouml: <https://w3id.org/ontouml#>
            PREFIX ontouml3: <https://w3id.org/ontouml-models/model/alpinebits2022#>
            SELECT ?sourcename ?scardinalityValue ?sourceid
            {
             ontouml3:$sourceEnd ?pred ontouml:Property .
             ontouml3:$sourceEnd ontouml:name ?sourcename .
             ontouml3:$sourceEnd ontouml:propertyType ?sourceid .
             ontouml3:${sourceEnd}_cardinality ontouml:cardinalityValue ?scardinalityValue .
            }
            """
            println("---sparqlQuerySourceRole:[$sparqlQuerySourceRole]")
            val ontoumlSourceRoles = queryList(sparqlQuerySourceRole,rdfDataDir).getOrThrow()
            if (ontoumlSourceRoles.size==0) continue
            val sourceRole = ontoumlSourceRoles[0]
            val sourceId = sourceRole["sourceid"].toString().substringAfterLast("#")
            val sourceTable = database.schema.tableId(sourceId)



            val targetEnd = entry["targetEnd"].toString().noBlank().substringAfterLast("#")
            println("---targetEnd:[$targetEnd]")
            val sparqlQueryTargetRole = """
            PREFIX ontouml2: <https://purl.org/ontouml-models/vocabulary/> 
            PREFIX ontouml: <https://w3id.org/ontouml#>
            PREFIX ontouml3: <https://w3id.org/ontouml-models/model/alpinebits2022#>
            SELECT ?targetname ?tcardinalityValue ?targetid
            {
             ontouml3:$targetEnd ?pred ontouml:Property .
             ontouml3:$targetEnd ontouml:name ?targetname .
             ontouml3:$targetEnd ontouml:propertyType ?targetid .
             ontouml3:${targetEnd}_cardinality ontouml:cardinalityValue ?tcardinalityValue .
            }
            """
            println("---sparqlQuerySourceRole:[$sparqlQueryTargetRole]")
            val ontoumlTargetRoles = queryList(sparqlQueryTargetRole,rdfDataDir).getOrThrow()
            if (ontoumlTargetRoles.size==0) continue
            val targetRole = ontoumlTargetRoles[0]
            val targetId = targetRole["targetid"].toString().substringAfterLast("#")
            val targetTable = database.schema.tableId(targetId)

            val sourceColName = sourceTable?.name?.noBlank() ?: continue
            val targetColName = targetTable?.name?.noBlank() ?: continue
            relationTable.name = "$sourceColName-$targetColName"
            if (relationTable.name.trim().isEmpty()) continue
            database.schema.tables().add(relationTable)
            relationTable.columns.add(Column.of(sourceColName))
            relationTable.columns.add(Column.of(targetColName))

            database.schema.addForeignKey("${relationTable.name}:$sourceColName-->${sourceTable.name}:$sourceColName")
            database.schema.addForeignKey("${relationTable.name}:$targetColName-->${targetTable.name}:$targetColName")

            println("RELATION [${sourceId}:${sourceTable?.name?:"??"}]-->[${targetId}:${targetTable?.name?:"??"}]")

            /*
             IS-A
             */

            val sparqlQueryIsA = """
                        PREFIX ontouml: <https://w3id.org/ontouml#> 
                        SELECT ?sbj ?specific ?general 
                        { 
                           ?sbj ?pred ontouml:Generalization . 
                           ?sbj ontouml:specific ?specific . 
                           ?sbj ontouml:general ?general 
                        }""".trimIndent()
            val ontoumlIsA = queryList(sparqlQueryIsA,rdfDataDir).getOrThrow()
            for (entry in ontoumlIsA) {
                println(entry)
                val isAId = entry["sbj"].toString().substringAfterLast("#")
                val genericId = entry["general"].toString().substringAfterLast("#")
                val specificId = entry["specific"].toString().substringAfterLast("#")
                val genericName = database.schema.tableId(genericId)?.name
                if (genericName==null){ println("not found $genericId"); continue}
                val specificName = database.schema.tableId(specificId)?.name
                if (specificName==null){ println("not found $specificId"); continue}
                println("GENERALIZATION [$specificName]->[$genericName]")
                database.schema.addIsA(generic = genericName,listOf(specificName),SchemaIsA.GENERIC.name)
            }


        }



        //database.schema.table()


    //    ontoumlClassNames.map { database.schema.tables().add()addTable("${it["name"]}:id") }

        // database.schema.tables().filter { it.hasLabel("") }

/*
        val foundStatements =
            findStatements("_", "_", "_",rdfDataDir)
        val foundClasses =
            findStatements("_", "https://purl.org/ontouml-models/vocabulary/name", "_",rdfDataDir).getOrDefault(emptyList())
     foundClasses
                .filter{ it.`object`.stringValue().isNotEmpty()}
                .mapIndexed { index, statement -> updateDb(database, statement) }
        //println(foundClasses.size)

        for (foundClass in foundClasses) {
            val foundClassesProperties =
                findStatements(foundClass.subject.stringValue(), "https://w3id.org/ontouml#class", "_", rdfDataDir).getOrDefault(emptyList())
            val table = database.schema.table(foundClass.`object`.stringValue())
            table?.addLabels(
                //foundClassesProperties.map { "${it.predicate.stringValue().substringAfterLast("/")} ${it.`object`.stringValue()}" }
                        foundClassesProperties.map { "${it.predicate.stringValue().substringAfterLast("/")} ${it.`object`.stringValue().substringAfterLast("/")}" }
            )
            println("----")
            println(table?.name)
            println("----")
            println(table?.labels)
            println("----")
        }
*/


        //findStatements(LabelField("_"), LabelField("_"), "https://purl.org/ontouml-models/vocabulary/Relation")
        //println(foundStatements)
        // for each relation compute roles properties and classes
        // compute role source property
        // compute role target property
        // select * ontouml isa
        return database
    }

}