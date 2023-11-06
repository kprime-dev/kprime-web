package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.trace.TraceName
import it.unibz.krdb.kprime.domain.trace.TraceService

class StatService(
    val sourceService: SourceService,
    val termService: TermService,
    val todoService: TodoService,
    val traceService: TraceService,
    val dataService: DataService
) {

    data class Vuca(val volatility:Int,
                    val uncertainty:Int,
                    val complexity: Int,
                    val ambiguity: Int)

    fun computeVUCA(trace:String): Vuca {
        // volatility
        // # changes
        var changes = 0
        for (change in dataService.getChangeSets(trace)) {
            changes += change.dropConstraint.size
            changes += change.dropTable.size
            changes += change.dropMapping.size
            changes += change.dropView.size
            changes += change.createConstraint.size
            changes += change.createTable.size
            changes += change.createMapping.size
            changes += change.createView.size
        }
        val volatility = changes

        // uncertainty
        // # todos
        val uncertainty = todoService.all(PrjContext.NO_WORKING_DIR).size

        // complexity
        // # tables #columns
        var tablesColumns = 0
        for (database in dataService.getDatabases(trace)) {
            tablesColumns += database.schema.tables?.size?:  0
            for (table in database.schema.tables?: arrayListOf()) {
                tablesColumns += table.columns.size
            }
        }
        val complexity = tablesColumns

        // ambiguity
        // # complexity - definitions
        var definitions = 0
        val traceFileNames = traceService.getTraceFileNames(trace)
        for (traceFileName in traceFileNames) {
            if (traceFileName.endsWith("_db.xml"))
                definitions += termService.getAllTerms(TraceName(trace), traceFileName, PrjContext.NO_PrjContext.location)
                        .filter { t -> t.description.isNotEmpty() }.size
        }
        val ambiguity = complexity - definitions

        return Vuca(volatility,uncertainty,complexity,ambiguity)
    }
}