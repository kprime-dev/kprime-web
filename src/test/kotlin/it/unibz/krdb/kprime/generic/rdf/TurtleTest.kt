package it.unibz.krdb.kprime.generic.rdf

import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.util.ModelBuilder
import org.eclipse.rdf4j.model.vocabulary.FOAF
import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.model.vocabulary.SHACL
import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.junit.Ignore
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.net.URL
import java.time.LocalDate
import kotlin.test.assertEquals


class TurtleTest {

    @Test
    fun test_read_csvw_local_resource() {
        val filename = "ttl/csvw.ttl"
        val input = this.javaClass.getResourceAsStream("/" + filename)
        val model = Rio.parse(input, "", RDFFormat.TURTLE)
//        for (std in model) {
//            print(std.subject.stringValue()+ " ")
//            print(std.predicate.stringValue()+ " ")
//            println(std.`object`.stringValue())
//        }
        assertEquals(632,model.size)
    }

    @Test
    @Ignore
    fun test_read_schema_local_resource() {
        val filename = "ttl/schema.ttl"
        val input = this.javaClass.getResourceAsStream("/" + filename)
        val model = Rio.parse(input, "", RDFFormat.TURTLE)
//        for (std in model) {
            //if (std.predicate.stringValue().contains("rangeIncludes")) {
//                print(std.subject.stringValue() + " ")
//                print(std.predicate.stringValue() + " ")
//                println(std.`object`.stringValue())
            //}
//        }
        assertEquals(8706,model.size)
//        for (res in model.filter(null,RDF.TYPE,null)) {
//            println(res.subject.stringValue())
//        }

        val factory = SimpleValueFactory.getInstance()
        var personPropertiesResult = ""
        for (res in model.filter(factory.createIRI("http://schema.org/Person"), SHACL.PROPERTY,null)) {
            personPropertiesResult += "${res.subject.stringValue()} ${res.`object`.stringValue()}\n"
        }
        assertEquals("""
         http://schema.org/Person http://schema.org/Person-additionalName
         http://schema.org/Person http://schema.org/Person-address
         http://schema.org/Person http://schema.org/Person-affiliation
         http://schema.org/Person http://schema.org/Person-alumniOf
         http://schema.org/Person http://schema.org/Person-award
         http://schema.org/Person http://schema.org/Person-awards
         http://schema.org/Person http://schema.org/Person-birthDate
         http://schema.org/Person http://schema.org/Person-birthPlace
         http://schema.org/Person http://schema.org/Person-brand
         http://schema.org/Person http://schema.org/Person-callSign
         http://schema.org/Person http://schema.org/Person-children
         http://schema.org/Person http://schema.org/Person-colleague
         http://schema.org/Person http://schema.org/Person-colleagues
         http://schema.org/Person http://schema.org/Person-contactPoint
         http://schema.org/Person http://schema.org/Person-contactPoints
         http://schema.org/Person http://schema.org/Person-deathDate
         http://schema.org/Person http://schema.org/Person-deathPlace
         http://schema.org/Person http://schema.org/Person-duns
         http://schema.org/Person http://schema.org/Person-email
         http://schema.org/Person http://schema.org/Person-familyName
         http://schema.org/Person http://schema.org/Person-faxNumber
         http://schema.org/Person http://schema.org/Person-follows
         http://schema.org/Person http://schema.org/Person-funder
         http://schema.org/Person http://schema.org/Person-gender
         http://schema.org/Person http://schema.org/Person-givenName
         http://schema.org/Person http://schema.org/Person-globalLocationNumber
         http://schema.org/Person http://schema.org/Person-hasCredential
         http://schema.org/Person http://schema.org/Person-hasOccupation
         http://schema.org/Person http://schema.org/Person-hasOfferCatalog
         http://schema.org/Person http://schema.org/Person-hasPOS
         http://schema.org/Person http://schema.org/Person-height
         http://schema.org/Person http://schema.org/Person-homeLocation
         http://schema.org/Person http://schema.org/Person-honorificPrefix
         http://schema.org/Person http://schema.org/Person-honorificSuffix
         http://schema.org/Person http://schema.org/Person-interactionStatistic
         http://schema.org/Person http://schema.org/Person-isicV4
         http://schema.org/Person http://schema.org/Person-jobTitle
         http://schema.org/Person http://schema.org/Person-knows
         http://schema.org/Person http://schema.org/Person-knowsAbout
         http://schema.org/Person http://schema.org/Person-knowsLanguage
         http://schema.org/Person http://schema.org/Person-makesOffer
         http://schema.org/Person http://schema.org/Person-memberOf
         http://schema.org/Person http://schema.org/Person-naics
         http://schema.org/Person http://schema.org/Person-nationality
         http://schema.org/Person http://schema.org/Person-netWorth
         http://schema.org/Person http://schema.org/Person-owns
         http://schema.org/Person http://schema.org/Person-parent
         http://schema.org/Person http://schema.org/Person-parents
         http://schema.org/Person http://schema.org/Person-performerIn
         http://schema.org/Person http://schema.org/Person-publishingPrinciples
         http://schema.org/Person http://schema.org/Person-relatedTo
         http://schema.org/Person http://schema.org/Person-seeks
         http://schema.org/Person http://schema.org/Person-sibling
         http://schema.org/Person http://schema.org/Person-siblings
         http://schema.org/Person http://schema.org/Person-sponsor
         http://schema.org/Person http://schema.org/Person-spouse
         http://schema.org/Person http://schema.org/Person-taxID
         http://schema.org/Person http://schema.org/Person-telephone
         http://schema.org/Person http://schema.org/Person-vatID
         http://schema.org/Person http://schema.org/Person-weight
         http://schema.org/Person http://schema.org/Person-workLocation
         http://schema.org/Person http://schema.org/Person-worksFor

        """.trimIndent(),personPropertiesResult)

//        for (res in model.filter(null, factory.createIRI(":", "rangeIncludes"),null)) {
//            println(res.subject.stringValue())
//        }

//        for (res in model.filter(null, factory.createIRI("http://www.w3.org/ns/shacl#", "datatype"),null)) {
//            println(res.subject.stringValue())
//        }
    }

    @Test
    fun test_read_cube_local_resource() {
        val filename = "ttl/rdf-schema.ttl"
        val input = this.javaClass.getResourceAsStream("/" + filename)
        val model = Rio.parse(input, "", RDFFormat.TURTLE)
        for (std in model) {
            val subject = std.subject
            //println(subject.stringValue())
        }
    }

    @Test
    @Ignore
    // It requires Trig dependency.
    fun test_read_write_gufo_local_resource() {
        val filename = "ttl/gufo.ttl"
        val input = this.javaClass.getResourceAsStream("/" + filename)
        val model = Rio.parse(input, "", RDFFormat.TURTLE)
        println(model.size)
        val  out = ByteArrayOutputStream()
        Rio.write(model,out, RDFFormat.TRIG)
        assertEquals("""
            @prefix ex: <http://myonto.org/> .
        """.trimIndent(),String(out.toByteArray()))

    }

    @Test
    @Ignore
    fun test_read_cube_test() {
        val documentUrl = URL("https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.ttl")
        val model = Rio.parse(documentUrl.openStream(), documentUrl.toString(), RDFFormat.TURTLE)
        printTTLResult(model)

    }

    @Test
    @Ignore
    fun test_read_turtle_string() {
        val turtle = """
                @prefix ex: <http://myonto.org/> .
                @prefix sh: <http://myonto.com/> .
                
                ex:PersonShape
                    a sh:NodeShape ;
                    sh:targetClass ex:Person ;    # Applies to all persons
                    sh:property [                 # _:b1
                        sh:path ex:ssn ;           # constrains the values of ex:ssn
                        sh:maxCount 1 ;
                        sh:datatype xsd:string ;
                        sh:pattern "^\\d{3}-\\d{2}-\\d{4}${'$'}" ;
                    ] ;
                    sh:property [                 # _:b2
                        sh:path ex:worksFor ;
                        sh:class ex:Company ;
                        sh:nodeKind sh:IRI ;
                    ] ;
                    sh:closed true ;
                    sh:ignoredProperties ( rdf:type ) .            
        """.trimIndent()
        val model = Rio.parse(StringReader(turtle), "", RDFFormat.TURTLE)

        val  out = ByteArrayOutputStream()
        Rio.write(model,out, RDFFormat.TURTLE)
        assertEquals("""
            @prefix ex: <http://myonto.org/> .
            @prefix sh: <http://myonto.com/> .
            
            ex:PersonShape a sh:NodeShape;
              sh:closed true;
              sh:ignoredProperties _:node1f38417qax3 .
            
            _:node1f38417qax3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#first> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>;
              <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest> <http://www.w3.org/1999/02/22-rdf-syntax-ns#nil> .
            
            ex:PersonShape sh:property _:node1f38417qax1 .
            
            _:node1f38417qax1 sh:datatype <http://www.w3.org/2001/XMLSchema#string>;
              sh:maxCount 1;
              sh:path ex:ssn;
              sh:pattern "^\\d{3}-\\d{2}-\\d{4}${'$'}" .
            
            ex:PersonShape sh:property _:node1f38417qax2 .
            
            _:node1f38417qax2 sh:class ex:Company;
              sh:nodeKind sh:IRI;
              sh:path ex:worksFor .
            
            ex:PersonShape sh:targetClass ex:Person .
            """.trimIndent(),String(out.toByteArray()))

    }

    @Test
    fun test_build_turtle_model() {
            val ex = "http://myonto.org/"
            val builder = ModelBuilder()
            .setNamespace("ex","http://myonto.org/")
            .setNamespace("ox","http://ibm.org/")
            .subject("ex:Picasso")
            .add("ex:creationDate", LocalDate.parse("1885-04-01"))
                    .add("ox:street","4 Novembre 95")
                    .add("ex:street","4 Novembre 94")
            val model = builder.build()
            val  out = ByteArrayOutputStream()
            Rio.write(model,out, RDFFormat.TURTLE)
            assertEquals("""
                @prefix ex: <http://myonto.org/> .
                @prefix ox: <http://ibm.org/> .
                @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                
                ex:Picasso ox:street "4 Novembre 95";
                  ex:creationDate "1885-04-01";
                  ex:street "4 Novembre 94" .
                
            """.trimIndent(),String(out.toByteArray()))
    }

    @Test
    @Ignore
    fun test_write_cube_test() {
        val documentUrl = URL("https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.ttl")
        val model = Rio.parse(documentUrl.openStream(), documentUrl.toString(), RDFFormat.TURTLE)
        val  out = ByteArrayOutputStream()
        //printTTLResult(model)
        Rio.write(model,out,RDFFormat.TURTLE)
        println(String(out.toByteArray()))
    }

    private fun printTTLResult(results: Model?) {
        if (results==null) return;
        println(results.size)
        for (result in results) {
            println(result.`object`.stringValue())
        }
    }

    @Test
    fun test_model_build() {
        val builder = ModelBuilder()

// set some namespaces
        builder.setNamespace("", "http://example.org/").setNamespace(FOAF.NS)

        builder.namedGraph(":graph1") // add a new named graph to the model
            .subject(":john") // add  several statements about resource ex:john
            .add(FOAF.NAME, "John") // add the triple (ex:john, foaf:name "John") to the named graph
            .add(FOAF.AGE, 42)
            .add(FOAF.MBOX, "john@example.org")

// add a triple to the default graph
        builder.defaultGraph().add(":graph1", RDF.TYPE, ":Graph")

// return the Model object
        val model = builder.build()
        val  out = ByteArrayOutputStream()
        Rio.write(model,out, RDFFormat.TURTLE)
        assertEquals("""
            @prefix : <http://example.org/> .
            @prefix foaf: <http://xmlns.com/foaf/0.1/> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            
            :john foaf:age "42"^^xsd:int;
              foaf:mbox "john@example.org";
              foaf:name "John" .
            
            :graph1 a :Graph .
            
        """.trimIndent(),String(out.toByteArray()))
    }

    @Test
    fun test_model_build_jsonld() {
        val builder = ModelBuilder()

// set some namespaces
        builder.setNamespace("", "http://example.org/").setNamespace(FOAF.NS)

        builder.namedGraph(":graph1") // add a new named graph to the model
            .subject(":john") // add  several statements about resource ex:john
            .add(FOAF.NAME, "John") // add the triple (ex:john, foaf:name "John") to the named graph
            .add(FOAF.AGE, 42)
            .add(FOAF.MBOX, "john@example.org")

// add a triple to the default graph
        builder.defaultGraph().add(":graph1", RDF.TYPE, ":Graph")

// return the Model object
        val model = builder.build()
        val  out = ByteArrayOutputStream()
        Rio.write(model,out, RDFFormat.JSONLD)
        assertEquals("""
            [ {
              "@graph" : [ {
                "@id" : "http://example.org/john",
                "http://xmlns.com/foaf/0.1/age" : [ {
                  "@type" : "http://www.w3.org/2001/XMLSchema#int",
                  "@value" : "42"
                } ],
                "http://xmlns.com/foaf/0.1/mbox" : [ {
                  "@value" : "john@example.org"
                } ],
                "http://xmlns.com/foaf/0.1/name" : [ {
                  "@value" : "John"
                } ]
              } ],
              "@id" : "http://example.org/graph1",
              "@type" : [ "http://example.org/Graph" ]
            } ]
        """.trimIndent(),String(out.toByteArray()))
    }
}