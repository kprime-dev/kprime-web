package it.unibz.krdb.kprime.generic.rdf

import org.eclipse.rdf4j.model.vocabulary.RDF4J
import org.eclipse.rdf4j.repository.RepositoryException
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.RDFParseException
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.sail.shacl.ShaclSail
import org.junit.Test
import kotlin.test.assertEquals

class ShaclTest {

    @Test
    fun test_shacl_valid_data() {
        val memoryStore = MemoryStore()
        val shalSail = ShaclSail(memoryStore)
        val repo = SailRepository(shalSail)

        val shaclRules = ShaclTest::class.java.getResource("/shacl/shacl-artists-shapes.ttl")
        val dataOk = ShaclTest::class.java.getResource("/shacl/data-artists.ttl")

        val connection = repo.getConnection()


        connection.use {
            try {
                connection.begin()
                connection.clear(RDF4J.SHACL_SHAPE_GRAPH)
                connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH)
                connection.add(dataOk.openStream(), dataOk.toExternalForm(), RDFFormat.TURTLE)
            } catch (ex: RepositoryException) {
                ex.printStackTrace()
            }
        }
    }

    @Test
    fun test_shacl_invalid_data() {
        // given
        val memoryStore = MemoryStore()

        val shaclSail = ShaclSail(memoryStore)
        shaclSail.setLogValidationViolations(false)

        val repo = SailRepository(shaclSail)

        val shaclRules = ShaclTest::class.java.getResource("/shacl/shacl-artists-shapes.ttl")
        val dataKo = ShaclTest::class.java.getResource("/shacl/data-artists-invalid.ttl")

        val connection = repo.getConnection()

        // when
        try {
            connection.use {
                connection.begin()
                connection.clear(RDF4J.SHACL_SHAPE_GRAPH)
                connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH)
                connection.add(dataKo.openStream(), dataKo.toExternalForm(), RDFFormat.TURTLE)
            }
        }
        // then
        catch (parseEx: RDFParseException) {

            val cause = parseEx.message
            assertEquals("Namespace prefix 'ex' used but not defined [line 3]",cause)
        }
    }
}