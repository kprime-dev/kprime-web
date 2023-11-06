package it.unibz.krdb.kprime.view.controller

import it.unibz.krdb.kprime.domain.*
import it.unibz.krdb.kprime.domain.trace.TraceService
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.term.TermService
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.domain.user.UserService

class ChartController(
    val dataService: DataService,
    val traceService: TraceService,
    val todoService: TodoService,
    val prjContextService: PrjContextService,
    val userService: UserService,
    val termService: TermService,
    val rdfService: RdfService
)
    : ChartBarHandlers by ChartBarController(dataService),
      ChartGanttHandlers by ChartGanttController(todoService),
      ChartCaseHandlers by ChartCaseController(todoService, prjContextService, userService, rdfService),
      ChartTraceHandlers by ChartTraceController(traceService,termService, prjContextService,userService,dataService, rdfService),
      ChartCircleHandlers by ChartCircleController(userService,prjContextService,todoService,termService,traceService),
      ChartForceTreeHandlers by ChartForceTreeController(userService,prjContextService,todoService,termService,traceService)
