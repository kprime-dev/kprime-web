package it.unibz.krdb.kprime.adapter.chart

fun storyAddUrlLine(line: String, index: Int, markdownDiag: String): Pair<Int, String> {
    var index1 = index
    var markdownDiag1 = markdownDiag
    if (line.contains("](")) {
        val activityTitle = extractTitle(line)
        val activityUrl = extractUrl(line)
        index1++
        markdownDiag1 += "id$index1([$activityTitle]);" + System.lineSeparator()
        markdownDiag1 += "click id$index1 \"${activityUrl}\" \"Go to\"" + System.lineSeparator()
        if (activityUrl.endsWith(".md"))
            markdownDiag1 += "class id$index1 yellow" + System.lineSeparator()
        else
            markdownDiag1 += "class id$index1 red" + System.lineSeparator()
        markdownDiag1 += "id$index1-->"
    }
    return Pair(index1, markdownDiag1)
}

private fun extractTitle(line: String): String {
    val start = line.indexOf("[")+1
    val end = line.indexOf("]",start)
    return line.substring(start,end)
}

private fun extractUrl(line: String): String {
    val start = line.indexOf("](")+2
    val end = line.indexOf(")",start)
    return line.substring(start,end)
}
