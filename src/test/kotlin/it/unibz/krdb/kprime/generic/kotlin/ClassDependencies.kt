package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test
import java.io.File

class ClassDependencies {

    @Test
    fun test_class_dependencies() {
        val directory = File("/home/nipe/Workspaces/semint-kprime-webapp/")
        val files = directory.walkTopDown().filter { it.isFile && it.extension == "kt" }
        val classMap = mutableMapOf<String, MutableSet<String>>()
        files.forEach { file ->
            val className = file.nameWithoutExtension
            val classDependencies = mutableSetOf<String>()
            file.forEachLine { line ->
                if (line.startsWith("import ")) {
                    val dependency = line.substringAfter("import ").substringBeforeLast(".")
                    classDependencies.add(dependency)
                }
            }
            classMap[className] = classDependencies
        }
        classMap.forEach { (className, dependencies) ->
            println("$className depends on ${dependencies.joinToString(", ")}")
        }
    }


    @Test
    fun test_class_usage_counter() {
        val directory = File("/home/nipe/Workspaces/semint-kprime-webapp/")
        val files = directory.walkTopDown().filter { it.isFile && it.extension == "kt" }
        val classMap = mutableMapOf<String, Int>()
        files.forEach { file ->
            file.forEachLine { line ->
                if (line.startsWith("import ")) {
                    val className = line.substringAfter("import ").substringBeforeLast(".")
                    classMap[className] = (classMap[className] ?: 0) + 1
                }
            }
        }
        classMap.toList().sortedByDescending { (_, count) -> count }.forEach { (className, count) ->
            println("$className used $count times")
        }
    }

    @Test
    fun test_branches_conter() {
        val directory = File("/home/nipe/Workspaces/semint-kprime-webapp/")
        val files = directory.walkTopDown().filter { it.isFile && it.extension == "kt" }
        val classMap = mutableMapOf<String, Int>()
        var branchCount = 0
        files.forEach { file ->
            file.forEachLine { line ->
                if (line.contains("if") || line.contains("else") || line.contains("case") || line.contains("default") || line.contains("while") || line.contains("for") || line.contains("do")) {
                    val className = file.name
                    classMap[className] = (classMap[className] ?: 0) + 1
                    //branchCount++
                }
            }
        }
        classMap.toList().sortedByDescending { (_, count) -> count }.forEach { (className, count) ->
            println("$className branches $count times")
        }
//        println("The code has $branchCount logical branches.")
    }
}

