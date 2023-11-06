package it.unibz.krdb.kprime.adapter.chart

fun storyAddTitleLine(line: String, index: Int, markdownDiag: String): Pair<Int, String> {
    var index1 = index
    var markdownDiag1 = markdownDiag
    if (line.startsWith("#")) {
        val activityTitle = line.replace("#", "")
        index1++
        markdownDiag1 += "tid$index1[$activityTitle];" + System.lineSeparator()
        markdownDiag1 += "class tid$index1 green" + System.lineSeparator()
        markdownDiag1 += "tid$index1-->"
    }
    return Pair(index1, markdownDiag1)
}
