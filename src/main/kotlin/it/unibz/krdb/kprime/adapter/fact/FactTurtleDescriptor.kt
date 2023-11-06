package it.unibz.krdb.kprime.adapter.fact

import it.unibz.krdb.kprime.domain.term.Term
import org.eclipse.rdf4j.model.util.ModelBuilder
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import unibz.cs.semint.kprime.domain.db.Database
import java.io.ByteArrayOutputStream
import java.time.LocalDate

class FactTurtleDescriptor {

    fun describe(db: Database,terms:List<Term>):String {
        val builder = ModelBuilder()
                .setNamespace("ex","http://myonto.org/${db.name}/")
        for (term in terms) {
            var termName = term.name
            builder.subject("ex:${term.name}")
                    .add("ex:definitionDate", LocalDate.now())
                    .add("sh:description", term.description)
            val table = db.schema.table(termName)?.apply {
                for (col in this.columns) {
                    builder.add("ex:${col.name}", col.default?:"")
                    builder.add("sh:class", col.type?:"xsd:string")
                }
            }
        }

        val model = builder.build()
        val  out = ByteArrayOutputStream()
        Rio.write(model,out, RDFFormat.TURTLE)
        return String(out.toByteArray())
    }
}