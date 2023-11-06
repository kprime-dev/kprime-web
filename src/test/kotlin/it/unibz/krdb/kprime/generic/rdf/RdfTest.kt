package it.unibz.krdb.kprime.generic.rdf

import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.rio.RDFFormat
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import java.net.URL

class RdfTest {

    private val logger = LoggerFactory.getLogger("Mio")

    val personURL = "https://schema.org/Person.rdf"
    val cubeURL = "https://raw.githubusercontent.com/UKGovLD/publishing-statistical-data/master/specs/src/main/vocab/cube.rdf"

    @Test
    @Ignore
    fun test_import_rdf() {
        val documentURL = URL(personURL)
        val openStream = documentURL.openStream()
        val subjects = mutableSetOf<String>()
        try {
            val res = QueryResults.parseGraphBackground(openStream,
                    documentURL.toString(),
                    RDFFormat.RDFXML)
            while(res.hasNext()) {
                val next = res.next()
                //println("${next.subject} ${next.predicate} ${next.`object`}")
                if (next.predicate.toString().contains("range"))
                    subjects.add(next.subject.toString().substringAfterLast("/")+" %%% "+
                            next.`object`.toString().substringAfterLast("/"))
            }
        } finally {
            openStream.close()
        }
        subjects.forEach{println(it)}
    }
}