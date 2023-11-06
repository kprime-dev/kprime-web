package it.unibz.krdb.kprime.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.term.LabelField
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import java.sql.ResultSet
import java.util.LinkedHashMap

class RdfJdbcPrinter(val iriContext:String, val rdfService: RdfService, val database: Database, val table: Table, val rdfDataDir:String) {

    fun printJsonLDResultSet(resultSet: ResultSet):String {
        val list = mutableListOf<Map<String, String>>()
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        val contextObj = LinkedHashMap<String, String>()
        contextObj.put("ex","http://example.org/${database.name}#")

        while( resultSet.next()) {
            val obj = LinkedHashMap<String, String>()
            obj.put("@id", table.name)
            rdfService.findStatements(iriContext, LabelField(table.name), LabelField("_"), "_", rdfDataDir)
                .map { liststd -> liststd.forEach{ std -> obj["@${std.predicate}"] = std.cobject } }
            for (i in 1..columnCount) {
                obj.put("ex:"+metaData.getColumnName(i), resultSet.getString(i))
            }
            list.add(obj)
        }

        val graphObj = LinkedHashMap<String, Any>()
        graphObj.put("@context", contextObj)
        graphObj.put("@graph", list)

        val mapper = ObjectMapper()
        val result = mapper.writeValueAsString(graphObj)
        println(result)
        return result
    }


}