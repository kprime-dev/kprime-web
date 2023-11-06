package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.search.SearchService
import javax.servlet.http.HttpServletResponse

class SearchController( val searchService: SearchService) {

    val getTermSearch = Handler { ctx ->
        val text = ctx.pathParam("text")
        val searchResult = searchService.findTerm(text)
        ctx.json(searchResult)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }


}