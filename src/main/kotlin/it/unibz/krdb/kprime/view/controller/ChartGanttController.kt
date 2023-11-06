package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.chart.ChartGanttService
import it.unibz.krdb.kprime.domain.todo.TodoService

interface ChartGanttHandlers {
    val getGanttChart: Handler
}

class ChartGanttController(todoService: TodoService) : ChartGanttHandlers {

    val chartGanttService = ChartGanttService(todoService)

    override val getGanttChart =  Handler { ctx ->
        val traceName = "Todo" //ctx.pathParam("traceName")
        val traceFileName = "Diagram" //ctx.pathParam("traceFileName")
        var fileContent = chartGanttService.chartGantt(traceName, traceFileName)
        ctx.html(fileContent)
    }

}