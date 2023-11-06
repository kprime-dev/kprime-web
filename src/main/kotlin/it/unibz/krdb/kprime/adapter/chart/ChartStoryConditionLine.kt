package it.unibz.krdb.kprime.adapter.chart

fun storyAddConditionLine(line: String, index: Int, markdownDiag: String): Pair<Int, String> {
    var index1 = index
    var markdownDiag1 = markdownDiag
    if (line.startsWith("+ go ")) {
        var activityTitle = line.replace("+ go ", "")
        val gotoId = line.split(" ")[2]
        activityTitle = activityTitle.replace(gotoId, "")
        index1++
        markdownDiag1 += "id$index1{{$activityTitle}};" + System.lineSeparator()
        markdownDiag1 += "id$index1{{$activityTitle}}-->id$gotoId;" + System.lineSeparator()
        markdownDiag1 += "class id$index1 green" + System.lineSeparator()
        markdownDiag1 += "id$index1-->"
    }
    return Pair(index1, markdownDiag1)
}

