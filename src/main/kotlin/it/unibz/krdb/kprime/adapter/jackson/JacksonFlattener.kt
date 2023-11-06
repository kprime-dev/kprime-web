package it.unibz.krdb.kprime.adapter.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType

typealias JsonTable = MutableMap<String, Any>

object JacksonFlattener {


    fun computeSchema(
        tree: JsonNode,
        path: String = "",
        row: Int = 0,
        tables: MutableMap<String, JsonTable>,
        currentTable: MutableMap<String, Any>
    ) {
        when (tree.nodeType) {
            JsonNodeType.OBJECT -> {
                tree.fields().forEach { (key, value) ->
                    println("OBJECT [$path.$key.$row]")
                    tables[path] = currentTable
                    computeSchema(value, "$path.$key", row, tables, currentTable)
                }
            }
            JsonNodeType.ARRAY -> {
                var pos = 0
                tree.elements().forEach { value ->
                    //println("ARRAY [$path]")
                    pos++
                    val existingTable = tables[path]
                    if (existingTable==null) {
                        val newTable = mutableMapOf<String, Any>()
                        tables[path] = newTable
                        computeSchema(value, path, pos, tables, newTable)
                    } else computeSchema(value, path, pos, tables, existingTable)
                }
            }
            JsonNodeType.STRING -> {
                currentTable[path]="Text"
                println(tree.asText())
            }
            JsonNodeType.NUMBER -> {
                currentTable[path]="Double"
                //println(tree.asDouble())
            }
            JsonNodeType.BOOLEAN -> {
                currentTable[path]="Boolean"
                //println(tree.asBoolean())
            }
            JsonNodeType.NULL -> {
                println("null")
            }
            else -> {
                println("Unknown node type")
            }
        }
    }



    fun computeInstances(
        tree: JsonNode,
        path: String = "",
        row: Int = 0,
        tables: MutableMap<String, JsonTable>,
        currentTable: MutableMap<String, Any>
    ) {
        when (tree.nodeType) {
            JsonNodeType.OBJECT -> {
                tree.fields().forEach { (key, value) ->
                    //println("OBJECT [$path.$key.$row]")
                    computeInstances(value, "$path$row.$key", row, tables, currentTable)
                }
            }
            JsonNodeType.ARRAY -> {
                var pos = 0
                tree.elements().forEach { value ->
                    //println("ARRAY [$path]")
                    pos++
                    val newTable = mutableMapOf<String,Any>()
                    tables[path+pos] = newTable
                    computeInstances(value, path, pos, tables, newTable)
                }
            }
            JsonNodeType.STRING -> {
                currentTable["$path$row"]=tree.asText()
                //println(tree.asText())
            }
            JsonNodeType.NUMBER -> {
                currentTable["$path$row"]=tree.asDouble()
                //println(tree.asDouble())
            }
            JsonNodeType.BOOLEAN -> {
                currentTable["$path$row"]=tree.asBoolean()
                //println(tree.asBoolean())
            }
            JsonNodeType.NULL -> {
                println("null")
            }
            else -> {
                println("Unknown node type")
            }
        }
    }

}