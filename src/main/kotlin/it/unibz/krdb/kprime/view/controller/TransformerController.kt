package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.transformer.TransformerDesc
import it.unibz.krdb.kprime.domain.transformer.TransformerService
import javax.servlet.http.HttpServletResponse

class TransformerController(private val transformerService: TransformerService) {

    data class Rename(val oldName:String, val newName:String)

    val deleteTransformer =  Handler { ctx ->
        val transformerName = ctx.pathParam("transformerName")
        transformerService.deleteTransformer(transformerName)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }
    val renameTransformer = Handler { ctx->
        val rename = ctx.bodyAsClass<Rename>()
        val transformerDesc = transformerService.getTransformer(rename.oldName)
        transformerDesc.name=rename.newName
        transformerService.deleteTransformer(rename.oldName)
        transformerService.putTransformer(transformerDesc)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }

    val putTransformer = Handler { ctx ->
        val transformerDesc = ctx.bodyAsClass<TransformerDesc>()
        transformerService.putTransformer(transformerDesc)
        ctx.status(HttpServletResponse.SC_ACCEPTED)
    }
    val getTransformer = Handler { ctx ->
        val transformerName = ctx.pathParam("transformerName")
        val transformerDescs = transformerService.getTransformer(transformerName)
        ctx.json(transformerDescs)
    }

    val getTransformerNames = Handler { ctx ->
        val transformerNames = transformerService.getAllTransformerNames()
        ctx.json(transformerNames)
    }
}
