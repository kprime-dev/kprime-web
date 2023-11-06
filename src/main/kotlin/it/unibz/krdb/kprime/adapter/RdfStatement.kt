package it.unibz.krdb.kprime.adapter

import org.eclipse.rdf4j.model.Statement

data class RdfStatement(
    val subject:String,
    val predicate:String,
    val cobject:String,
    val context:String
) {

    companion object {
        fun from4jStatement(std: Statement):RdfStatement {
            return RdfStatement(
                std.subject.stringValue(),
                std.predicate.stringValue(),
                std.`object`.stringValue(),
                std.context?.toString()?:"null")
        }
    }
}
