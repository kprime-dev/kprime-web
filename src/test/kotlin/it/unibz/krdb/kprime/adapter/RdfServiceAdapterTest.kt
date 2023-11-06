package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.adapter.jackson.file.SettingFileRepository
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.term.LabelField
import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.rio.RDFFormat
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class RdfServiceAdapterTest {

    @Test
    @Ignore
    fun test_rdf_list() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/abc/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        // when
        val result = adapter.list("","/home/nipe/Temp/kprime/abc/")
        // then
        assertEquals(
            "(http://www.w3.org/1999/02/22-rdf-syntax-ns#src  http://www.w3.org/1999/02/22-rdf-syntax-ns#pred  http://www.w3.org/1999/02/22-rdf-syntax-ns#obj) [null]\n" +
                    ", (http://www.w3.org/2000/01/rdf-schema#aaa  http://www.w3.org/2000/01/rdf-schema#bbb  http://www.w3.org/2000/01/rdf-schema#ccc) [http://localhost:h2sakila]\n" +
                    ", (:xxx  :yyy  \":zzz\") [http://localhost:h2sakila]\n",
            result.getOrNull())
    }

    @Test
    @Ignore
    fun test_rdf_listStatements() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        // when
        val result = adapter.listStatements()
        // then
        assertEquals("RdfStatement(subject=http://www.w3.org/1999/02/22-rdf-syntax-ns#src, predicate=http://www.w3.org/1999/02/22-rdf-syntax-ns#pred, cobject=http://www.w3.org/1999/02/22-rdf-syntax-ns#obj, context=null)",
            result.getOrThrow().joinToString())
    }

    @Test
    @Ignore
    fun test_rdf_find() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        // when
        val result = adapter.find(iriContext = "",
            LabelField(":xxx"),LabelField("_"),"_",
            rdfDataDir = "/home/nipe/Temp/kprime/settings/")
        // then
        assertEquals("""
            (:xxx  :yyy  ":zzz") [http://localhost:h2sakila]
            , (:xxx  :y2  ":z2") [http://localhost:h2sakila]
            
        """.trimIndent(),
        result.getOrNull())
    }

    @Test
    @Ignore
    fun test_find_rdf_statements() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        // when
        val foundStatements = adapter.findStatements( iriContext = ""
            , LabelField(":xxx"), LabelField("_"), "_"
            , rdfDataDir = "").getOrThrow()
        // then
        assertEquals("xxx",LabelField(foundStatements[0].subject).suffix())
        assertEquals("yyy",LabelField(foundStatements[0].predicate).suffix())
        assertEquals("zzz",LabelField(foundStatements[0].cobject).suffix())
    }

    @Test
    @Ignore
    fun test_import_namespace() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataDir = "/home/nipe/Temp/rdf4j/"
        // when
        val result = adapter.import(
            "http://test.yyy.org/",
            PrjContextLocation("/home/nipe/Temp/"),"alpinebit2022_ontology.ttl",
            rdfDataDir
        )
        // then
        assertEquals("Labels from alpinebit2022_ontology.ttl added.",result.getOrThrow())
    }

    @Test
    @Ignore
    fun test_read() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // then
        println(adapter.listRdfStatements(rdfDataPath))
    }

    @Test
    @Ignore
    fun test_find_namespace_all() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // then
        println(adapter.internalFindStatements("_","_","_", rdfDataPath,iriContext = "")
            .getOrDefault(emptyList())
            .joinToString(System.lineSeparator()))
    }

    @Test
    @Ignore
    fun test_find_namespace_classes() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // then
        println(adapter.internalFindStatements("_","https://w3id.org/ontouml#name","_",rdfDataPath, iriContext = "")
            .getOrDefault(emptyList())
            .joinToString(System.lineSeparator()))
    }

    @Test
    @Ignore
    fun test_remove_namespace() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        val namespace = "http://test.yyy.org/"
        // then
        println(adapter.removeNamespace(namespace,rdfDataPath).getOrThrow())
    }

    @Test
    @Ignore
    fun test_databaseFromOntouml() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        val namespace = "http://test.yyy.org/"
        // when
        val db = adapter.toDatabaseFromOntouml(namespace,rdfDataPath,addLabels = false)
        // then
        println(db)
        assertEquals(91,db.schema.tables!!.size)
    }

    @Test
    @Ignore
    fun test_read_alpinebits_local_resource() {
        val filename = "alpinebits22.ttl"
        val input = this.javaClass.getResourceAsStream("/ttl/" + filename)

//        val model = Rio.parse(input, "", RDFFormat.TURTLE)
//        for (std in model) {
//            println(" ${std.subject.stringValue()} ${std.predicate.stringValue()} ${std.`object`.stringValue()}")
//        }

        val res = QueryResults.parseGraphBackground(input, "", RDFFormat.TURTLE)
        while (res.hasNext()) {
            val std = res.next()
            //val contx = std.context.stringValue()
            val subje = std.subject.stringValue()
            val pred = std.predicate.stringValue()
            val obje = std.`object`.stringValue()

            val generalizations = true
            val classes = false
            val relations = false
            val noName = false
            val entity = false
            val enitity_code= "Ocg3Uf6AUB0C9C23"
            val property = false
            val property_code= "rI7OZeaGAqACBxaQ"
            val cardinality = property_code+"_cardinality"
            val objecto = false
            val objecto_code= "Ocg3Uf6AUB0C9C23"

            if (classes && pred.equals("https://purl.org/ontouml-models/vocabulary/name") && obje.isNotEmpty())
                println("$subje $pred $obje")


            if (noName && pred.equals("https://purl.org/ontouml-models/vocabulary/name") && obje.isEmpty())
                println("$subje $pred $obje")

            if (relations && obje.equals("https://purl.org/ontouml-models/vocabulary/Relation"))
                println("$subje $pred $obje")

            if (generalizations && obje.equals("https://purl.org/ontouml-models/vocabulary/Generalization"))
                println("$subje $pred $obje")

            if (property && subje.equals("http://purl.org/ontouml-models/dataset/alpinebits2022/$property_code"))
                println(" $subje $pred $obje")

            if (property && subje.equals("http://purl.org/ontouml-models/dataset/alpinebits2022/$cardinality"))
                println(" $subje $pred $obje")

            if (entity && subje.equals("http://purl.org/ontouml-models/dataset/alpinebits2022/$enitity_code"))
                println(" $subje $pred $obje")

            if (objecto && obje.equals("http://purl.org/ontouml-models/dataset/alpinebits2022/$objecto_code"))
                println(" $subje $pred $obje")

        }
    }

    @Test
    @Ignore
    fun test_find_ontouml_properties() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // when query all
        val sbj = "https://w3id.org/ontouml-models/model/alpinebits2022#rI7OZeaGAqACBxaQ"
        val result = adapter.find(iriContext = "",
            //LabelField("http://purl.org/ontouml-models/dataset/alpinebits2022/rI7OZeaGAqACBxaQ"),LabelField("_"),"_",rdfDataPath)
            // LabelField("_"),LabelField("_"),"_",rdfDataPath)
            LabelField(sbj),LabelField("_"),"_",rdfDataPath)
        println(result)
        val result2 = adapter.find(iriContext = "",
            //LabelField("http://purl.org/ontouml-models/dataset/alpinebits2022/rI7OZeaGAqACBxaQ"),LabelField("_"),"_",rdfDataPath)
            // LabelField("_"),LabelField("_"),"_",rdfDataPath)
            LabelField(sbj+"_cardinality"),LabelField("_"),"_",rdfDataPath)
        println(result2)
    }

    @Test
    @Ignore
    fun test_sparql_query() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // when query all
        val result = adapter.query(
            """
            PREFIX alpinebits: <http://purl.org/ontouml-models/dataset/alpinebits2022/>
            PREFIX alpi: <https://w3id.org/ontouml-models/model/alpinebits2022>
            SELECT ?sbj
            WHERE {
                ?sbj ?pred ?cob
            }
            """, rdfDataPath
        ).fold(
            onSuccess = { println(it) },
            onFailure = { it.printStackTrace() }
        )
    }

    @Test
    @Ignore
    fun test_sparql_query_ontouml_class_names() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // when query all
        //                 alpinebits:6o5aPf6AUB0C9BBY ?pred ?cob
        // alpi:FEwkFeaGAqACByKS rdf:type ontouml:Diagram
        // alpi:FEwkFeaGAqACByKS ?pred ?cob .
        adapter.queryList("""
            PREFIX alpinebits: <http://purl.org/ontouml-models/dataset/alpinebits2022/>
            PREFIX alpi: <https://w3id.org/ontouml-models/model/alpinebits2022#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX ontouml: <https://w3id.org/ontouml#> 
            SELECT ?name ?sbj
            {
             ?sbj ?pred ontouml:Class .
             ?sbj ontouml:name ?name 
            }
            """,rdfDataPath).fold(
            onSuccess = { println(it) },
            onFailure = { it.printStackTrace() }
        )
        // then
        //assertEquals("",result)
    }

    @Test
    @Ignore
    fun test_sparql_query_ontouml_relations() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        // when query all
        //                 alpinebits:6o5aPf6AUB0C9BBY ?pred ?cob
        // alpi:FEwkFeaGAqACByKS rdf:type ontouml:Diagram
        // alpi:FEwkFeaGAqACByKS ?pred ?cob .
        adapter.queryList(
            """
            PREFIX alpinebits: <http://purl.org/ontouml-models/dataset/alpinebits2022/>
            PREFIX alpi: <https://w3id.org/ontouml-models/model/alpinebits2022#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX ontouml: <https://w3id.org/ontouml#> 
            SELECT ?name ?sbj ?sourceEnd ?targetEnd ?stereotype
            {
             ?sbj ?pred ontouml:Relation .
             ?sbj ontouml:sourceEnd ?sourceEnd .
             ?sbj ontouml:targetEnd ?targetEnd .
             ?sbj ontouml:stereotype ?stereotype .
             ?sbj ontouml:name ?name .
            }
            """, rdfDataPath
        ).fold(
            onSuccess = { println(it) },
            onFailure = { it.printStackTrace() }
        )
    }

    @Test
    @Ignore
    fun test_sparql_query_ontouml_roles() {
        // given
        val settingRepository = SettingFileRepository("/home/nipe/Temp/kprime/settings/")
        val settingService = SettingService(settingRepository)
        val adapter = RdfServiceAdapter(settingService)
        val rdfDataPath = "/home/nipe/Temp/rdf4j/"
        val propertyId = "oVhRFRaGAqCsIA5V"
        //val propertyId = "4XBRFRaGAqCsIA4_"
        // when query all
        //                 alpinebits:6o5aPf6AUB0C9BBY ?pred ?cob
        // alpi:FEwkFeaGAqACByKS rdf:type ontouml:Diagram
        // alpi:FEwkFeaGAqACByKS ?pred ?cob .
        adapter.queryList(
            """
            PREFIX alpinebits: <http://purl.org/ontouml-models/dataset/alpinebits2022/>
            PREFIX alpi: <https://w3id.org/ontouml-models/model/alpinebits2022#>
            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            PREFIX ontouml: <https://w3id.org/ontouml#> 
            SELECT ?pred ?obj
            {
             alpi:$propertyId ?pred ?obj .
            }
            """, rdfDataPath
//        adapter.queryList(
//                    """
//            PREFIX alpinebits: <http://purl.org/ontouml-models/dataset/alpinebits2022/>
//            PREFIX alpi: <https://w3id.org/ontouml-models/model/alpinebits2022#>
//            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//            PREFIX ontouml: <https://w3id.org/ontouml#>
//            SELECT ?name ?cardinalityValue ?lowerBound ?upperBound
//            {
//             alpi:$propertyId ?pred ontouml:Property .
//             alpi:$propertyId ontouml:name ?name .
//             alpi:${propertyId}_cardinality ontouml:cardinalityValue ?cardinalityValue .
//             alpi:${propertyId}_cardinality ontouml:lowerBound ?lowerBound .
//             alpi:${propertyId}_cardinality ontouml:upperBound ?upperBound .
//            }
//            """, rdfDataPath
        ).fold(
            onSuccess = { println(it.map { it.toString() }.joinToString(System.lineSeparator())) },
            onFailure = { it.printStackTrace() }
        )
    }

}