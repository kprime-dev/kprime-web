package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.domain.todo.TodoService

class ChartGoalsService(val todoService: TodoService) {

    fun chartCase(prjContext: PrjContext?, userRole:String): String {
        val prjContextLocation = prjContext?.location?: PrjContext.NO_WORKING_DIR
        val todos = todoService.all(prjContextLocation)
        val tasks = todoService.tasks(prjContextLocation)
        var markdownDiag = ""
        val lineSeparator = System.lineSeparator()
        markdownDiag += "classDef actorstyle fill:#${ChartColors.user},stroke:#333,stroke-width:2px;" + lineSeparator
        markdownDiag += "classDef goalstyle fill:#${ChartColors.goal},stroke:#333,stroke-width:2px;" + lineSeparator
        markdownDiag += "classDef sourcestyle fill:#${ChartColors.source},stroke:#333,stroke-width:2px;" + lineSeparator
        markdownDiag += "classDef taskstyle fill:#${ChartColors.task},stroke:#333,stroke-width:2px;" + lineSeparator
        var i = 0
        var j = 0
        for (todo in todos) {
            markdownDiag += "id${todo.id}([${todo.title}])" + lineSeparator
            i++
            val task = tasks[todo.id.toString()]
            if (task!=null) {
                markdownDiag += "class id${todo.id} taskstyle" + lineSeparator
                if (prjContext!=null && !prjContext.isNoProject()) {
                    ///noteview.html?pr=prova4&tr=traces___root&tf=base.md
                    //markdownDiag += "click id${todo.id} \"/project/${project.name}/slide/${task.traceName}/${task.storyFileName}\" \"Goto to task story.\"" + lineSeparator
                    markdownDiag += "click id${todo.id} \"/noteview.html?pr=${prjContext.name}&tr=${task.traceName}&tf=${task.storyFileName}\" \"Goto to task story.\"" + lineSeparator
                } else {
                    markdownDiag += "click id${todo.id} \"/slide/${task.traceName}/${task.storyFileName}\" \"Goto to task story.\"" + lineSeparator
                }
                val assignee = task.assignee
                if (assignee.isNotEmpty()) {
                    j++
                    markdownDiag += "actor${assignee}>actor:${assignee}]" + lineSeparator
                    markdownDiag += "id${todo.id}-.->actor${assignee}" + lineSeparator
                    markdownDiag += "class actor${assignee} actorstyle" + lineSeparator
                }
                for (source in task.sources) {
                    j++
                    markdownDiag += "source${source}>source:${source}]" + lineSeparator
                    markdownDiag += "id${todo.id}-.->source${source}" + lineSeparator
                    markdownDiag += "class source${source} sourcestyle" + lineSeparator
                }
            } else {
                markdownDiag += "class id${todo.id} goalstyle" + lineSeparator
            }
            if (todo.partof.isNotEmpty()) {
                if (todo.labelsAsString().isEmpty()) {
                    markdownDiag += "id${todo.partof}-->id${todo.id}" + lineSeparator
                } else {
                    markdownDiag += "id${todo.partof}-- ${todo.labelsAsString()} -->id${todo.id}" + lineSeparator
                }
            }
        }
        var menu = ""
        if (userRole != User.ROLE.ANONYMOUS.name) {
            menu = """
<nav aria-label="breadcrumb">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="/" >Home</a></li>
        <li class="breadcrumb-item active"><a href="/todos.html" >Goals</a></li>
    </ol>
</nav> 
            """.trimIndent()
        }
        var fileContent = ChartGoalsService::class.java.getResource("/public/chart-goals.html").readText()
        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        fileContent = fileContent.replace("{{menu}}", menu)
        fileContent = fileContent.replace("{{project.name}}", prjContext?.name?:"")
        return fileContent
    }

}