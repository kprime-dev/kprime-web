package it.unibz.krdb.kprime.adapter.chart

fun storyAddSourceLine(line: String, index: Int, markdownDiag: String): Pair<Int, String> {
    var index1 = index
    var markdownDiag1 = markdownDiag
    if (line.startsWith("+ source ")) {
        val activityTitle = line.replace("+ source ", "")
        index1++
        markdownDiag1 += "id$index1[($activityTitle)];" + System.lineSeparator()
        markdownDiag1 += "class id$index1 green" + System.lineSeparator()
        markdownDiag1 += "id$index1-->"
    }
    return Pair(index1, markdownDiag1)
}