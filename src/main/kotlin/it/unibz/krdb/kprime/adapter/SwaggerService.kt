package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.adapter.chart.ChartEntityService

class SwaggerService {

    fun htmlSwagger(contextName:String): String {
        //val jsonApiUrl = "https://petstore3.swagger.io/api/v3/openapi.json"
        //val jsonApiUrl = "http://localhost:7000/context/projectName/dictionary/traceName/traceFileName/jsonapi"
        val jsonApiUrl = "/context/$contextName/dictionary/traceName/traceFileName/jsonapi"
        var fileContent = SwaggerService::class.java.getResource("/public/swagger.html").readText()
        fileContent = fileContent.replace("{{jsonApiUrl}}", jsonApiUrl)
        return fileContent

    }
}