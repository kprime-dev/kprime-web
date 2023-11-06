package it.unibz.krdb.kprime.support

fun String.substring(start:String,end:String):String {
    return this.substringAfter(start).substringBefore(end)
}

fun String.extraString(start:String, end:String):String {
    return this.substringBefore(start)+ this.substringAfter(end)
}

fun String.alfaNumeric(alternative:String = ""): String {
    val nonAlphaNum = "[^a-zA-Z0-9]".toRegex()
    return replace(nonAlphaNum, alternative)
}

fun String.camelToSnakeCase(): String {
    val pattern = "(?<=.)[A-Z]".toRegex()
    return this.replace(pattern, "_$0").lowercase()
}

fun String.snakeToCamelCase(): String {
    val pattern = "_[a-z]".toRegex()
    return replace(pattern) { it.value.last().uppercase() }
}

fun String.dashToCamelCase(): String {
    val pattern = "-[a-zA-Z]".toRegex()
    return replace(pattern) { it.value.last().uppercase() }
}

fun String.noBlank(): String {
    return replace(" ","")
}
