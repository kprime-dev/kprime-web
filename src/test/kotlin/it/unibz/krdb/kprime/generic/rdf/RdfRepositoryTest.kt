package it.unibz.krdb.kprime.generic.rdf

import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.model.Statement
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.query.BindingSet
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.sail.nativerdf.NativeStore
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.io.InputStream
import kotlin.test.assertEquals

class RdfRepositoryTest {

 @Test
 fun test_save_rdf_repo() {
        // given
         val filename = "cube.ttl";
         val input : InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);
         val model : Model = Rio.parse(input, "", RDFFormat.TURTLE);

         // when
         val db : Repository = SailRepository(MemoryStore())
         db.connection.use { conn ->
             conn.add(model)
             }

         // then
         db.connection.use { conn ->
             conn.getStatements(null, null, null).use { result ->
    //             for (st : Statement in result) {
    //               println("db contains: $st")
    //             }
                 val st = result.next()
                 assertEquals("""
                     (http://purl.org/linked-data/cube, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.w3.org/2002/07/owl#Ontology) [null]
                 """.trimIndent(),st.toString())
               }
             }
         db.shutDown()
     }

    @Test
    fun test_save_rdf_repo_direct() {
        // given
        val filename = "cube.ttl";
        val input : InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);

        // when
        val db : Repository = SailRepository(MemoryStore())
        db.connection.use { conn ->
            conn.add(input, "", RDFFormat.TURTLE)
        }

        // then
        db.connection.use { conn ->
            conn.getStatements(null, null, null).use { result ->
                //             for (st : Statement in result) {
                //               println("db contains: $st")
                //             }
                val st = result.next()
                assertEquals("""
                     (http://purl.org/linked-data/cube, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.w3.org/2002/07/owl#Ontology) [null]
                 """.trimIndent(),st.toString())
            }
        }
        db.shutDown()
    }

    @Test
    fun test_list_rdf_repo_direct() {
        // given
        val filename = "cube.ttl";
        val input: InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);
        val db: Repository = SailRepository(MemoryStore())
        db.connection.use { conn ->
            conn.add(input, "", RDFFormat.TURTLE)
        }
        // then
        db.connection.use { conn ->
            conn.getStatements(null, null, null).use { result ->
                //             for (st : Statement in result) {
                //               println("db contains: $st")
                //             }
                val st = result.next()
                assertEquals("""
                     (http://purl.org/linked-data/cube, http://www.w3.org/1999/02/22-rdf-syntax-ns#type, http://www.w3.org/2002/07/owl#Ontology) [null]
                 """.trimIndent(),st.toString())
            }
        }
        db.shutDown()
    }

    @Test
    fun test_select_rdf_repo_direct() {
        // given
        val filename = "cube.ttl";
        val input : InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);
        val db : Repository = SailRepository(MemoryStore())
        db.connection.use { conn ->
            conn.add(input, "", RDFFormat.TURTLE)
        }
        val queryString = """
            PREFIX owl: <http://www.w3.org/2002/07/owl#>
            PREFIX rdf-ns: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            SELECT ?s
            WHERE  { ?s rdf-ns:type owl:Ontology . }
        """.trimIndent()
        // when
        var toCheck = ""
        db.connection.use { conn ->
            val query = conn.prepareTupleQuery(queryString)
            query.evaluate().use { result ->
                for (solution: BindingSet in result) {
                    toCheck = solution.getValue("s").toString()
                }
            }
        }
        db.shutDown()
        // then
        assertEquals("http://purl.org/linked-data/cube",toCheck)
    }



    @Test
    fun test_construct_rdf_repo_direct() {
        // given
        val filename = "cube.ttl";
        val input : InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);
        val db : Repository = SailRepository(MemoryStore())
        db.connection.use { conn ->
            conn.add(input, "", RDFFormat.TURTLE)
        }
        val queryString = """
            PREFIX owl: <http://www.w3.org/2002/07/owl#>
            PREFIX rdf-ns: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
            CONSTRUCT
            WHERE  { ?s rdf-ns:type owl:Ontology . }
        """.trimIndent()
        // when
        var toCheck = ""
        db.connection.use { conn ->
            val query = conn.prepareGraphQuery(queryString)
            query.evaluate().use { result ->
                for (solution: Statement in result) {
                    toCheck = solution.subject.toString()
                }
            }
        }
        db.shutDown()
        // then
        assertEquals("http://purl.org/linked-data/cube",toCheck)
    }

    @Test
    @Ignore
    fun test_list_rdf_native_repo_direct() {
        // given
        //val filename = "cube.ttl";
        //val input: InputStream = this.javaClass.getResourceAsStream("/ttl/" + filename);
        val dataDir = File("/home/nipe/Temp/kprime/rdf4j/")
        //val dataDir = File("/home/nicola/Tmp/kprime/rdf4j/")
        //val db: Repository = SailRepository(MemoryStore())
        val db: Repository = SailRepository(NativeStore(dataDir))
        // then
        db.connection.use { conn ->
            val factory = SimpleValueFactory.getInstance()
            //val resultMessage = conn.getStatements(factory.createIRI("rdf:src"), null, null).use { result ->
                val resultMessage = conn.getStatements(null, null, null).use { result ->
                            // for (st : Statement in result) {
                            //   println("db contains: $st")
                            // }
                            result.joinToString()
            }
            println(resultMessage)
        }
        db.shutDown()
    }

}