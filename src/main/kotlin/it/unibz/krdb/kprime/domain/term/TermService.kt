package it.unibz.krdb.kprime.domain.term

import com.github.jsonldjava.core.JsonLdProcessor
import com.github.jsonldjava.core.RDFDataset
import com.github.jsonldjava.utils.JsonUtils
import it.unibz.krdb.kprime.domain.vocabulary.VocabularyService
import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.exception.ItemNotFoundException
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextName
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.vocabulary.Vocabulary
import it.unibz.krdb.kprime.domain.vocabulary.VocabularyServices
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.collections.ArrayList


class TermService(
    val settingService: SettingService,
    private val termRepo: TermRepositoryBuilder,
    val dataService: DataService,
    val rdfService: RdfService,
    val prjContextService: PrjContextService,
    private val jsonApiService: JsonApiService
):
        VocabularyServices by VocabularyService(settingService),
    TermServices {

    private val kpRepoDir = ".kprime/"

    override fun writeAllTerms(projectLocation: String, traceName: TraceName, traceFileName:String, newterms: List<Term>) {
        termRepo.build(projectLocation + kpRepoDir).saveAll(newterms)
    }

    override fun listAllTerms(projectLocation: String, traceName: TraceName, traceFileName: String): List<Term> {
        if (projectLocation.isEmpty() || traceName.isEmpty() || traceFileName.isEmpty()) return emptyList()
        return termRepo.build(projectLocation + kpRepoDir).findAll()
    }

    override fun getAllTerms(traceName: TraceName, traceFileName: String, projectLocation: String): List<Term> {
        val database = dataService.getDatabase(PrjContextLocation(projectLocation), traceName.value, traceFileName ).getOrElse { Database().withGid() }
        val tables = database.schema.tables ?: return emptyList()
        println(" tableTerms [${traceName}] [${traceFileName}] [${projectLocation}]")
        val tableTerms = allAsMap(termRepo.build(projectLocation + kpRepoDir).findAll())
        println(" tableTerms.size ${tableTerms.size}")
        extractTermsFromTables(tables, tableTerms)
        println(" tableTerms.size with tables ${tableTerms.size}")
        extractTermsFromMappings(database, tableTerms)
        updateWithLabels(tableTerms,PrjContextLocation(projectLocation))
        println(" tableTerms.size with mappings ${tableTerms.size}")
        val expandAllTerms = expandAllTerms(tableTerms.values.toList().sortedBy { it.name }, readInstanceVocabularies())
        println(" tableTerms.size expanded vocabularies ${expandAllTerms.size}")
        return expandAllTerms
    }

    fun markdown(projectLocation: String): Result<Unit> {
        //val all =  termRepo.build(projectLocation + kpRepoDir).findAll()
        val all =  getAllTerms(TraceName("root"),"base",projectLocation)
        var termText = "ID | Description | Type "+System.lineSeparator()
        termText +=  "|---|---|---|"+System.lineSeparator()
        termText += all.map { "${it.name} | ${it.description} | ${it.type}" }.joinToString(System.lineSeparator())
        return kotlin.runCatching {
            File(projectLocation+"terms.md").writeText(termText)
        }
    }

    private fun allAsMap(terms:List<Term>) = terms.map { it.name to it }.toMap().toMutableMap()

    private fun updateWithLabels(tableTerms: MutableMap<String, Term>, prjContextLocation: PrjContextLocation) {
        for (termName in tableTerms.keys) {
            val term = tableTerms[termName]
            val rdfDataDir = RdfService.getPrjContextRdfDataDir(prjContextLocation)
            if (term!=null) {
                rdfService.find(iriContext = "",
                    LabelField(termName),LabelField("_"),"_",
                    rdfDataDir = rdfDataDir)
                    .onSuccess { rdfLabels ->
                        tableTerms[termName] = term.copy(labels = rdfLabels)
                }
            }
        }
    }

    override fun addTerm(projectLocation: String, term: Term):Result<String> {
        termRepo.build(projectLocation + kpRepoDir).save(term)
        return Result.success(term.gid)
    }

    override fun remTerm(projectLocation: String, termName: String):Result<String> {
        val nrDeleted = termRepo.build(projectLocation + kpRepoDir).delete { it.name == termName }
        if (nrDeleted>0) return Result.success("Deleted.")
        else return Result.failure(ItemNotFoundException("Not deleted."))
    }

    override fun updateTerm(projectLocation: PrjContextLocation, newTerm: Term):Result<String> {
        val repo = termRepo.build(projectLocation.value + kpRepoDir)
            .update(newTerm, { it.name == newTerm.name })
        return Result.success("Updated ${newTerm.name}.")
    }

    private fun extractTermsFromMappings(database: Database, tableTerms: MutableMap<String, Term>) {
        for (mapping in database.mappings()) {
            val term = tableTerms[mapping.name]
            if (term==null) {
                tableTerms[mapping.name] = Term(
                    mapping.name,
                    "mapping",
                    "",
                    "",
                    "",
                    SQLizeSelectUseCase().sqlize(mapping),
                    ""
                )
            }
        }
    }

    private fun extractTermsFromTables(tables: ArrayList<Table>, tableTerms: MutableMap<String, Term>) {
        for (table in tables) {

            val term = tableTerms[table.name]
            if (term == null) {
                tableTerms[table.name] = Term(
                    table.name,
                    "table",
                    "",
                    "",
                    "",
                    "",
                    table.labelsAsString()
                )
            }
            extractTermsFromTableColums(table, tableTerms)
        }
    }


//    // TODO replace with getAllTerms projectLocation
//    override fun listExpandedTerms(projectLocation: String, traceName: String, traceFileName: String): List<Term> {
//        val allTerms = listAllTerms(projectLocation, traceName, traceFileName)
//        println("allTerms.size ${allTerms.size}")
//        val allVocabulary = getAllVocabularies(traceName)
//        val allTermsExpanded = expandAllTerms(allTerms, allVocabulary)
//        println("allTermsExpanded.size ${allTermsExpanded.size}")
//        return allTermsExpanded.sortedBy { it.name }
//    }

    override fun listExpandedEntities(projectLocation: String, traceName: TraceName, traceFileName: String): List<Term> {
        val listExpandedTerms = getAllTerms(traceName, traceFileName, projectLocation)
        val termsNoAttributes = termsNoAttributes(listExpandedTerms)
        println("termsNoAttributesSorted.size ${termsNoAttributes.size}")
        return termsNoAttributes
    }

    private fun termsNoAttributes(allTermsExpanded: List<Term>) =
        allTermsExpanded.filter { !it.name.contains('.') }

    override fun getTermByGid(
        traceName: TraceName,
        traceFileName: String,
        projectLocation: String,
        gid: String
    ): Term? {
        return getAllTerms(traceName,traceFileName,projectLocation).firstOrNull { it.gid == gid }
    }

    override fun getTermByName(
        traceName: TraceName,
        traceFileName: String,
        projectLocation: String,
        name: String
    ): Result<Term> {
        val term = getAllTerms(traceName,traceFileName,projectLocation).firstOrNull { it.name == name }
        return if (term==null) Result.failure(ItemNotFoundException("Term ${name} not found."))
            else Result.success(term)
    }

    fun toTurtle(
        projectName: String,
        vocabularies: List<Vocabulary>,
        terms: List<Term>,
        iriContext: String,
        prjContextLocation: PrjContextLocation
    ): String {
        return rdfService.toTurtle(projectName,vocabularies,terms, iriContext, prjContextLocation)
    }

    companion object {

        internal fun extractTermsFromTableColums(table: Table, tableTerms: MutableMap<String, Term>) {
            for (col in table.columns) {
                val colTableName = "${table.name}.${col.name}"
                val term = tableTerms[colTableName]
                if (term == null) {
                    tableTerms[colTableName] = Term(
                        colTableName,
                        "column",
                        "",
                        col.type ?: "",
                        "",
                        "",
                        col.labels ?: ""
                    )
                }
            }
        }

        fun expandAllTerms(allTerms: List<Term>, allVocabulary: List<Vocabulary>): List<Term> {
            val prefixMaps = allVocabulary.associate { it.prefix to it.namespace }
            return allTerms.map { t -> expandPrefix(t,prefixMaps) }
        }

        fun expandPrefix(term: Term, prefixMaps: Map<Prefix, Namespace>): Term {
            term.typeExpanded = expandPrefix(term.type,prefixMaps)
            return term
        }

        fun expandPrefix(type: String, prefixMaps: Map<Prefix, Namespace>): String {
            for (prefix in prefixMaps.keys) {
                if (type.contains(prefix))
                    return type.replace(prefix,prefixMaps[prefix].toString())
            }
            return type
        }

        fun parseResourceCSV(resourceFileName:String):List<Term> {
            val input = this.javaClass.getResourceAsStream(resourceFileName) ?: throw IllegalArgumentException()
            val parsed = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(InputStreamReader(input))
            return parseCSVTerms(parsed)
        }


        fun parseFileCSV(fileName:String):List<Term> {
            val input = FileReader(fileName)
            val parsed = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(input)
            return parseCSVTerms(parsed)
        }

        private fun parseCSVTerms(parsed: CSVParser): MutableList<Term> {
            val terms = mutableListOf<Term>()
            for (record in parsed.records) {
                terms.add(
                    Term(
                        name = recordGet(record, "name"),
                        category = recordGet(record, "category"),
                        relation = recordGet(record, "relation"),
                        type = recordGet(record, "type"),
                        url = recordGet(record, "url"),
                        description = recordGet(record, "description"),
                        labels = recordGet(record, "labels")
                    )
                )
            }
            return terms
        }

        fun recordGet(record: CSVRecord, fieldName: String): String {
            return try {
                record.get(fieldName)
            } catch (e : Exception) {
                ""
            }
        }

        fun parseResourceTTL(resourceFileName:String):List<Term> {
            val input = this.javaClass.getResourceAsStream(resourceFileName)
            val model = Rio.parse(input, "", RDFFormat.TURTLE)
            return parseRIOTerms(model)
        }

        fun parseFileTTL(fileName:String):List<Term> {
            val input = FileReader(fileName)
            val model = Rio.parse(input, "", RDFFormat.TURTLE)
            return parseRIOTerms(model)
        }

        fun parseRemoteTTL(urlName:String):List<Term> {
            val url = URL(urlName)
            val input = url.openStream()
            val model = Rio.parse(input, "", RDFFormat.TURTLE)
            return parseRIOTerms(model)
        }

        private fun parseRIOTerms(parsed: Model): MutableList<Term> {
            val terms = mutableListOf<Term>()
            var oldSubject = ""
            var description = ""
            for (std in parsed) {
                val subject = std.subject.stringValue().substringAfterLast("#")
                if (oldSubject.isEmpty()) oldSubject = subject
                if (subject!=oldSubject) {
                    terms.add(
                        Term(
                            name = oldSubject,
                            category = "",
                            relation = "",
                            type = "",
                            url = "",
                            description = description,
                            labels = ""
                        )
                    )
                    oldSubject = subject
                    description = ""
                }
                if (std.predicate.stringValue().contains("comment")) {
                    description = std.`object`.stringValue()
                } else {
                    description += System.lineSeparator() + "${std.predicate.stringValue().substringAfterLast('/')} ${std.`object`.stringValue().substringAfterLast('/')}"
                }
            }
            return terms
        }

    }





    private val h2DataTypes : List<Term> = listOf<Term>(
        Term("INT","","","","https://www.h2database.com/html/datatypes.html#int_type","Possible values: -2147483648 to 2147483647.",""),
        Term("BOOLEAN","","","","","Possible values: TRUE, FALSE, and UNKNOWN (NULL).",""),
        Term("TINYINT","","","","","Possible values are: -128 to 127.",""),
        Term("SMALLINT","","","","","Possible values: -32768 to 32767.",""),
        Term("BIGINT","","","","","Possible values: -9223372036854775808 to 9223372036854775807.",""),
        Term("IDENTITY","","","","","Auto-Increment value. Possible values: -9223372036854775808 to 9223372036854775807. \n\t Used values are never re-used, even when the transaction is rolled back.",""),
        Term("DECIMAL","","","","","Data type with fixed precision and scale. This data type is recommended for storing currency values.",""),
        Term("DOUBLE","","","","","A floating point number. Should not be used to represent currency values, because of rounding problems. \n\t If precision value is specified for FLOAT type name, it should be from 25 to 53.",""),
        Term("REAL","","","","","A single precision floating point number. Should not be used to represent currency values, because of rounding problems.\n\t Precision value for FLOAT type name should be from 0 to 24.",""),
        Term("TIME","","","","","The format is hh:mm:ss[.nnnnnnnnn]. If fractional seconds precision is specified it should be from 0 to 9, 0 is default.",""),
        Term("TIME WITH TIME ZONE","","","","","",""),
        Term("DATE","","","","","","The proleptic Gregorian calendar is used."),
        Term("TIMESTAMP","","","","","",""),
        Term("TIMESTAMP WITH TIME ZONE","","","","","",""),
        Term("BINARY","","","","","Represents a byte array. For very long arrays, use BLOB.\n\t The maximum size is 2 GB, but the whole object is kept in memory when using this data type.",""),
        Term("OTHER","","","","","This type allows storing serialized Java objects. Internally, a byte array is used. ",""),
        Term("VARCHAR","","","","","A Unicode String. Use two single quotes ('') to create a quote.",""),
        Term("VARCHAR_IGNORECASE","","","","","Same as VARCHAR, but not case sensitive when comparing. ",""),
        Term("CHAR","","","","","The difference to VARCHAR is that trailing spaces are ignored and not persisted.",""),
        Term("BLOB","","","","","Unlike when using BINARY, large objects are not kept fully in-memory. ",""),
        Term("CLOB","","","","","Unlike when using VARCHAR, large CLOB objects are not kept fully in-memory; ",""),
        Term("UUID","","","","","Universally unique identifier. This is a 128 bit value. ",""),
        Term("ARRAY","","","","","An array of values. Maximum cardinality, if any, specifies maximum allowed number of elements in the array.",""),
        Term("ENUM","","","","","A type with enumerated values. Mapped to java.lang.Integer.\n\t Duplicate and empty values are not permitted.",""),
        Term("GEOMETRY","","","",""," If additional constraints are not specified this type accepts all supported types of geometries. ",""),
        Term("JSON","","","","","A RFC 8259-compliant JSON text.",""),
        Term("INTERVAL","","","","","Year-month intervals can store years and months. \n\tDay-time intervals can store days, hours, minutes, and seconds.\n" +
                "\t Year-month intervals are comparable only with another year-month intervals.\n" +
                "\t Day-time intervals are comparable only with another day-time intervals.","")
    )
    fun getH2Terms(): List<Term> { return h2DataTypes }



    fun getVocabularyTerms(traceName: TraceName, prefix: String):List<Term>? {
        val vocabularyTermMap = mutableMapOf<String,List<Term>>()
        println("getVocabularyTerms traceName:[$traceName] prefix: [$prefix]")
        val vocabulary = readInstanceVocabularies().firstOrNull { it.prefix == prefix } ?: return emptyList()
        println("getVocabularyTerms vocabulary [$vocabulary]")
        val reference = vocabulary.reference
        return if (vocabularyTermMap[prefix] != null) vocabularyTermMap[prefix]
        else tryLoadTerms(vocabularyTermMap, prefix, reference)
    }

    private fun tryLoadTerms(vocabularyTermMap: MutableMap<String, List<Term>>, prefix: String, reference: Reference): List<Term>? {
        println("tryLoadTerms vocabularyTermMap.size: [${vocabularyTermMap.size}] prefix:[$prefix] reference:[$reference]")
        if (reference.startsWith("http").and( reference.endsWith(".ttl"))) vocabularyTermMap[prefix] = parseRemoteTTL(reference)
        else if (reference.endsWith(".ttl")) vocabularyTermMap[prefix] = parseFileTTL(reference)
        return vocabularyTermMap[prefix]
    }

    fun schemaTerms() : List<Term> {
        val jsonObject = JsonUtils.fromInputStream(VocabularyService::class.java.getResourceAsStream("/vocabulary/schemaorg.jsonld"))
        val rdf = JsonLdProcessor.toRDF(jsonObject) as RDFDataset //,context,options)
        return rdf.getQuads("@default").map { q -> Term(q.subject.value?.substringAfterLast("/")?:"","",q.predicate.value?:"",q.`object`.value?:"",q.`object`.datatype?:"","",q.predicate.datatype?:"") }
    }

    fun jsonApi(projectName: PrjContextName, traceName:TraceName, traceFileName:String):Result<String> {
        val project = prjContextService.projectByName(projectName.value)?: return Result.failure(IllegalArgumentException("Project Not Found."))
        val database = dataService.getDatabase(PrjContextLocation(project.location),traceName.value,traceFileName).getOrDefault(Database().withGid())
        val allTerms = getAllTerms(traceName, traceFileName, project.location)
        val termsOpenApi = jsonApiService.openApi(project,database,allTerms)
        return Result.success(termsOpenApi)

    }
}