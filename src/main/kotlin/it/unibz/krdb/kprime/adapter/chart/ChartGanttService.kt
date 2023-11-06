package it.unibz.krdb.kprime.adapter.chart

import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.todo.TodoService
import java.text.SimpleDateFormat
import java.util.*

class ChartGanttService(val todoService: TodoService) {

    fun chartGantt(traceName: String, traceFileName: String): String {
        val allTodos = todoService.all(PrjContext.NO_WORKING_DIR)
        val todoGrouped = allTodos.groupBy { t -> t.key }
        var ganttTask = ""
        for (entry in todoGrouped) {
            var taskSection = entry.key
            val subTodos = entry.value
            if (taskSection.isEmpty()) taskSection = "Overall"
            ganttTask += "section ${taskSection}" + System.lineSeparator()
            for (i in 0..subTodos.size - 1) {
                val todo = subTodos[i]
                val taskName = todo.title
                var taskStart = SimpleDateFormat("YYYY-MM-dd").format(todo.dateOpened)
                if (taskSection.startsWith("1999")) taskStart = SimpleDateFormat("YYYY-MM-dd").format(Date())
                if (i > 0) taskStart = "after " + subTodos[i - 1].title
                val taskEstimate = todo.estimate
                if (taskEstimate > 0)
                    ganttTask += "${taskName} :${taskName} , ${taskStart} , ${taskEstimate}d" + System.lineSeparator()
            }
        }
        //        var markdownDiag="""
        //            title A Gantt Diagram
        //            dateFormat  YYYY-MM-DD
        //            section Section
        //            A task           :a1, 2014-01-01, 30d
        //            Another task     :after a1  , 20d
        //            section Another
        //            Task in sec      :2014-01-12  , 12d
        //            another task      : 24d
        //        """.trimIndent()
        var markdownDiag = """
                title ${traceName} ${traceFileName}
                dateFormat  YYYY-MM-DD 
            """.trimIndent() + System.lineSeparator()
        markdownDiag += ganttTask

        var fileContent = ChartGanttService::class.java.getResource("/public/gantt.html").readText()
        fileContent = fileContent.replace("{{markdownDiag}}", markdownDiag)
        return fileContent
    }
}