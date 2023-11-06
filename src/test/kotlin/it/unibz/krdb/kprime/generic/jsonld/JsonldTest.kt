package it.unibz.krdb.kprime.generic.jsonld

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jsonldjava.core.JsonLdOptions
import com.github.jsonldjava.core.JsonLdProcessor
import com.github.jsonldjava.core.RDFDataset
import com.github.jsonldjava.shaded.com.google.common.collect.ImmutableMap
import com.github.jsonldjava.utils.JsonUtils
import org.junit.Ignore
import org.junit.Test
import java.nio.charset.StandardCharsets


class JsonldTest {

    @Test
    @Ignore
    fun test_read_person_jsonld() {
        // given
        val jsonObject = JsonUtils.fromInputStream(JsonldTest::class.java.getResourceAsStream("/jsonld/person.jsonld"))
//        val context = mapOf<Object, Object>()
//        val options = JsonLdOptions()
        // when
        val rdf = JsonLdProcessor.toRDF(jsonObject) as RDFDataset//,context,options)
        // then
        println(JsonUtils.toPrettyString(jsonObject))
        println("----------------------------------------------")
        val toRDF = rdf.getQuads("@default").forEach { q -> println("${q.subject} ${q.predicate} ${q.`object`}") }
        println(JsonUtils.toPrettyString(toRDF))
    }


    @Test
    @Ignore
    fun test_read_person_jsonld_as_map() {
        try {
            // JSON string
            val json = String(JsonldTest::class.java.getResourceAsStream("/jsonld/person.jsonld").readAllBytes(), StandardCharsets.UTF_8)

            // convert JSON string to Java Map
            val map: HashMap<*, *> = ObjectMapper().readValue(json, HashMap::class.java)

            // print map keys and values
            for ((key, value) in map) {
                println("$key==${value.javaClass}")
            }
            println()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @Test
    @Ignore
    fun test_read_schemaorg_jsonld() {
        // given
        val jsonObject = JsonUtils.fromInputStream(JsonldTest::class.java.getResourceAsStream("/jsonld/schemaorg.jsonld"))
        // when
        val rdf = JsonLdProcessor.toRDF(jsonObject) as RDFDataset //,context,options)
        // then
        rdf.graphNames().forEach{println(it)}
        rdf.namespaces.forEach{ ns -> println(ns.key) }
        rdf.context.keys.forEach{ ck -> println(ck)}
        println(rdf.getQuads("@default").size)
        rdf.getQuads("@default").forEach { q -> println("${q.subject} ${q.predicate} ${q.`object`}") }
    }

    @Test
    @Ignore
    fun test_read_prov_jsonld() {
        // given
        val jsonObject = JsonUtils.fromInputStream(JsonldTest::class.java.getResourceAsStream("/jsonld/prov.jsonld"))
        // when
        val rdf = JsonLdProcessor.toRDF(jsonObject) as RDFDataset //,context,options)
        // then
        rdf.graphNames().forEach{println(it)}
        rdf.namespaces.forEach{ ns -> println(ns.key) }
        rdf.context.keys.forEach{ ck -> println(ck)}
        println(rdf.getQuads("@default").size)
        rdf.getQuads("@default").forEach { q -> println("${q.subject} ${q.predicate} ${q.`object`}") }
    }

    @Test
    @Ignore
    // can't download schema.org
    fun test_read_restautant_jsonld() {
        // given
        val jsonObject = JsonUtils.fromInputStream(JsonldTest::class.java.getResourceAsStream("/jsonld/restaurant.jsonld"))
        // when
        val rdf = JsonLdProcessor.toRDF(jsonObject) as RDFDataset //,context,options)
        // then
        rdf.graphNames().forEach{println(it)}
        rdf.namespaces.forEach{ ns -> println(ns.key) }
        rdf.context.keys.forEach{ ck -> println(ck)}
        println(rdf.getQuads("@default").size)
        rdf.getQuads("@default").forEach { q -> println("${q.subject} ${q.predicate} ${q.`object`}") }
    }

    @Test
    @Ignore
    fun test_jsonld_object() {
        //given
        val schemaOrg: Map<String, Any> = ImmutableMap.of<String, Any>("roleName",
                ImmutableMap.of("@id", "http://schema.org/roleName", "@type", "@id"))
        val value = listOf(mapOf(
                "@value" to "Production Company",
                "@value" to "4 Novembre 94"))
        val  input = mapOf(
                "roleName" to value,
                "money" to value,
                "@context" to schemaOrg
        )

        val context = mutableMapOf<String, String>()
        context["schema"]="http://schema.org/"
        val options = JsonLdOptions()
        // when
        println("----------flatten:")
        println(JsonLdProcessor.flatten(input, options))
        println("----------expand:")
        println(JsonLdProcessor.expand(input))
        println("----------compact:")
        println(JsonLdProcessor.compact(input, context, options))
    }

    @Test
    @Ignore
    // throws excpetion Caused by: java.net.MalformedURLException: no protocol:
    fun test_jsonld_graph() {
        // given
        val graph = """
{
  "@context": {
    "dc11": "http://purl.org/dc/elements/1.1/",
    "schema": "http://example.org/vocab#",
    "xsd": "http://www.w3.org/2001/XMLSchema#",
    "ex:contains": {
      "@type": "@id"
    }
  },
  "@graph": [
    {
      "@id": "http://example.org/library",
      "@type": "ex:Library",
      "ex:contains": "http://example.org/library/the-republic"
    },
    {
      "@id": "http://example.org/library/the-republic",
      "@type": "ex:Book",
      "dc11:creator": "Plato",
      "dc11:title": "The Republic",
      "ex:contains": "http://example.org/library/the-republic#introduction"
    },
    {
      "@id": "http://example.org/library/the-republic#introduction",
      "@type": "ex:Chapter",
      "dc11:description": "An introductory chapter on The Republic.",
      "dc11:title": "The Introduction"
    }
  ]
}            
        """.trimIndent()

        val frame = """
{
  "@context": {
    "dc": "http://purl.org/dc/elements/1.1/",
    "ex": "http://example.org/vocab#"
  },
  "@type": "ex:Library",
  "ex:contains": {
    "@type": "ex:Book",
    "ex:contains": {
      "@type": "ex:Chapter"
    }
  }
}            
        """.trimIndent()
        val options = JsonLdOptions()
        // when
        val result = JsonLdProcessor.frame(graph, frame, options)
        // then
        println(result)
    }
}