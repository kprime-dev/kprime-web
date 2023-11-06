package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.current.TransformerXUseCase

object TraceCmdTransApply: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trans-apply"
    }

    override fun getCmdDescription(): String {
        return "Apply trasformer to current database."
    }

    override fun getCmdUsage(): String {
        return "trans-apply <transformer-name> <decompose|compose>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,transform"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val tokens = command.split(" ")
        if (tokens.size!=3) return TraceCmdResult() failure "Required 2 arguments: <transformer-name> <decompose|compose>"
        val transformerName = tokens[1]
        val operation = tokens[2]
        val isDecompose = operation == "decompose"
        val isCompose = operation == "compose"
        if (!isDecompose && !isCompose) return TraceCmdResult() failure "Required <compose|decompose> as second argument."
        if (context.env.currentTrace==null) return TraceCmdResult() failure "Current trace null"
        val traceName = context.env.currentTrace!!
        //TraceService.RestApply(false, "applyTransformer: Database null.")
        val workingDir = context.pool.settingService.getWorkingDir()
        if (workingDir.isEmpty()) return TraceCmdResult() failure "Working dir empty."
        val allTransformerNames = context.pool.transformerService.getAllTransformerNames()
        if (!allTransformerNames.contains(transformerName)) return TraceCmdResult() failure "Trasformer name unkown. $allTransformerNames"
            //return TraceService.RestApply(false, "applyTransformer: Working dir empty.")
        val transformerDescriptor = context.pool.transformerService.getTransformerDescriptor(transformerName)
        try {
            val trasformer = TransformerXUseCase(
                    XMLSerializerJacksonAdapter(),
                    FileIOAdapter(),
                    workingDir + ".kprime/traces/" + traceName + "/",
                    workingDir + "transformers/" + transformerDescriptor.decomposeTemplate,
                    workingDir + "transformers/" + transformerDescriptor.decomposeMatcher,
                    workingDir + "transformers/" + transformerDescriptor.composeTemplate,
                    workingDir + "transformers/" + transformerDescriptor.composeMatcher,
                    traceName.value,
                    transformerName)
                    if (isDecompose)
                        trasformer.decompose(context.env.database, context.env.currentParams)
                    else
                        trasformer.compose(context.env.database, context.env.currentParams)
            context.env.currentParams.clear()
        } catch (e: Exception) {
            e.printStackTrace()
            return TraceCmdResult() failure e.message.toString()
            //return TraceService.RestApply(false, "applyTransformer: " + e.localizedMessage)
        }
        return TraceCmdResult() message "OK"
        //return TraceService.RestApply(true, "")
    }
}