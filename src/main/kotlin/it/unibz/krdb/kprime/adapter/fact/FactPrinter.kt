package it.unibz.krdb.kprime.adapter.fact

import unibz.cs.semint.kprime.domain.db.Table

class FactPrinter {

    companion object {

        fun print(table: Table, values:Map<String,Any>):String {
            var result = if (table.primaryKey.isNullOrEmpty() && table.naturalKey.isNullOrEmpty()) ""
                                else "(${table.primaryKey}/${table.naturalKey})"
            table.primaryKey
            for (col in table.columns) {
                result += " " + " ${col.role ?: ""} ${col.name.substringAfterLast("_")} '${values[col.name]}'".trim()
            }
            return result
        }

        fun print(table: Table, values:List<Any>):String {
            if (table.columns.size != values.size)
                return "Not matching values number (table size [${table.columns.size}] values number [${values.size}])."
            var result = if (table.primaryKey.isNullOrEmpty() && table.naturalKey.isNullOrEmpty()) ""
                                else "(${table.primaryKey}/${table.naturalKey})"
            table.columns.forEachIndexed { index, col ->
                result += " " + " ${col.role?:""} ${col.name.substringAfterLast("_")} '${values[index]}'".trim()
            }
            return result
        }

    }

}