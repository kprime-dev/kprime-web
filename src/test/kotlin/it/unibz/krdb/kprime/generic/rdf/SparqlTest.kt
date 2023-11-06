package it.unibz.krdb.kprime.generic.rdf

import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.vocabulary.FOAF
import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.query.explanation.Explanation
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.junit.Test
import java.lang.String
import kotlin.RuntimeException
import kotlin.test.assertEquals
import kotlin.use


class SparqlTest {

    @Test
    fun test_sparql() {

        val sailRepository = SailRepository(MemoryStore())

        sailRepository.connection.use { connection ->
            val vf = connection.valueFactory
            val ex = "http://example.com/"
            val peter: IRI = vf.createIRI(ex, "peter")
            val steve: IRI = vf.createIRI(ex, "steve")
            val mary: IRI = vf.createIRI(ex, "mary")
            val patricia: IRI = vf.createIRI(ex, "patricia")
            val linda: IRI = vf.createIRI(ex, "linda")
            connection.add(peter, RDF.TYPE, FOAF.PERSON)
            connection.add(peter, FOAF.KNOWS, patricia)
            connection.add(patricia, FOAF.KNOWS, linda)
            connection.add(patricia, FOAF.KNOWS, steve)
            connection.add(mary, FOAF.KNOWS, linda)
            connection.add(linda, FOAF.AGE, vf.createLiteral(8))
            connection.add(steve, FOAF.AGE, vf.createLiteral(18))
            connection.add(mary, FOAF.AGE, vf.createLiteral(28))
            connection.add(steve, FOAF.NAME, vf.createLiteral("Steve"))

            // Add some dummy data
            for (i in 0..99) {
                connection.add(vf.createBNode(i.toString() + ""), RDF.TYPE, FOAF.PERSON)
            }
            for (i in 0..999) {
                connection.add(vf.createBNode("${i % 150}"), FOAF.KNOWS, vf.createBNode("${i + 10}"))
            }
            for (i in 0..9) {
                connection.add(vf.createBNode("${i + 3}"), FOAF.AGE, vf.createLiteral(i + 10))
            }
            for (i in 0..3) {
                connection.add(vf.createBNode(i.toString() + ""), FOAF.NAME, vf.createLiteral("name$i"))
            }
        }

        sailRepository.connection.use { connection ->
            val query = connection.prepareTupleQuery(
                String.join(
                    "\n", "",
                    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>",
                    "SELECT ?friend ?name WHERE ",
                    "{",
                    "	BIND(<http://example.com/peter> as ?person)",
                    "	?person a foaf:Person ;",
                    "		(foaf:knows | ^foaf:knows)* ?friend.",
                    "	OPTIONAL {",
                    "		?friend foaf:name ?name",
                    "	}",
                    "	?friend foaf:age ?age",
                    "	FILTER(?age >= 18) ",
                    "}"
                )
            )
            val explain: Explanation = query.explain(Explanation.Level.Timed)
            //println(explain)
            val evaluate = query.evaluate()
            var result = ""
            evaluate.use {
                while (it.hasNext()) {
                    val bindings = it.next()
                    val friend = bindings.getValue("friend")
                    val name = bindings.getValue("name")
                    result +=">>>> $friend $name \n"
                }
            }
            assertEquals("""
                >>>> http://example.com/steve "Steve" 
                >>>> http://example.com/mary null 

            """.trimIndent(),result)
        }

        println("\n\n")

        sailRepository.connection.use { connection ->
            val query = connection.prepareTupleQuery(
                String.join(
                    "\n", "",
                    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>",
                    "SELECT * WHERE ",
                    "{",
                    "  BIND(<http://example.com/peter> as ?person)",
                    "	?person a foaf:Person .",
                    "	{",
                    "		?person	(foaf:knows | ^foaf:knows)* ?friend.",
                    "	} UNION {",
                    "		?friend foaf:age ?age",
                    "		FILTER(?age >= 18) ",
                    "	}",
                    "}"
                )
            )
            val explainUnoptimized: Explanation = query.explain(Explanation.Level.Unoptimized)
            //println(explainUnoptimized)
            //println("\n\n")
            val explain: Explanation = query.explain(Explanation.Level.Timed)
            //println(explain)
        }

        sailRepository.shutDown()
    }
}