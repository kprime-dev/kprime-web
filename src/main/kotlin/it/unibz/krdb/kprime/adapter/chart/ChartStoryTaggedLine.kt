package it.unibz.krdb.kprime.adapter.chart

fun storyAddTaggedLine(tag: String, line: String, index: Int, markdownDiag: String): Pair<Int, String> {
    var index1 = index
    var markdownDiag1 = markdownDiag
    if (line.startsWith("+ $tag ")) {
        val activityTitle = line.replace("+ $tag ", "")
        index1++
        markdownDiag1 += "id$index1>$tag: $activityTitle];" + System.lineSeparator()
        markdownDiag1 += "class id$index1 ${tag}style" + System.lineSeparator()
        markdownDiag1 += "id$index1-->"
    }
    return Pair(index1, markdownDiag1)
}

