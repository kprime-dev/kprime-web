package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext

import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.adapter.strategy.TransformationStrategyYesAdapter
import unibz.cs.semint.kprime.usecase.current.TransformerXUseCase

object TraceCmdTransApplicability: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "trans-applicability"
    }

    override fun getCmdDescription(): String {
        return "Computes the applicability of some transformer to current database."
    }

    override fun getCmdUsage(): String {
        return "trans-applicability"
    }

    override fun getCmdTopics(): String {
        return "read,logical,transform"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val workingDir = context.pool.settingService.getWorkingDir()
        if (workingDir.isEmpty()) return TraceCmdResult() failure "No working dir."
        val result = computeRestTransf(context)
        return TraceCmdResult() message computeApplicabilityResultMessage(result)

    }

    data class RestTransformApplicability(val name: String,
                                          val compose: Boolean,
                                          val composeWhy: String,
                                          val decompose: Boolean,
                                          val decomposeWhy: String)


    private fun computeApplicabilityResultMessage(result: List<RestTransformApplicability>) : String {
        var finalMessage = ""
        for (singleResult in result) {
            finalMessage += singleResult.name + " COMPOSE "+ singleResult.composeWhy + " DECOMPOSE " + singleResult.decomposeWhy + System.lineSeparator()
        }
        return finalMessage
    }

    private fun computeApplicabilityMessage(message: String, tranformerParmeters: MutableMap<String, Any>): String {
        var mappingMessage = ""
        for (entry in tranformerParmeters) {
            mappingMessage += " ${entry.key} ${entry.value}" + System.lineSeparator()
        }
        return message + System.lineSeparator() + mappingMessage
    }

    fun computeRestTransf(context: CmdContext): MutableList<RestTransformApplicability> {
        val result = mutableListOf<RestTransformApplicability>()
        val allTransformerNames = context.pool.transformerService.getAllTransformerNames()
        val workingDir = context.pool.settingService.getWorkingDir()
        for (transformerName in allTransformerNames) {
            val transformerDescriptor = context.pool.transformerService.getTransformerDescriptor(transformerName)
            val transformerXUseCase = TransformerXUseCase(
                    XMLSerializerJacksonAdapter(),
                    FileIOAdapter(),
                    workingDir + ".kprime/traces/" + context.env.currentTrace + "/",
                    workingDir + "transformers/" + transformerDescriptor.decomposeTemplate,
                    workingDir + "transformers/" + transformerDescriptor.decomposeMatcher,
                    workingDir + "transformers/" + transformerDescriptor.composeTemplate,
                    workingDir + "transformers/" + transformerDescriptor.composeMatcher,
                    context.env.currentTrace?.value?:"",
                    transformerName)
            val composeApplicable = transformerXUseCase
                    .composeApplicable(
                            context.env.database, TransformationStrategyYesAdapter(), context.env.currentParams)
            val decomposeApplicable = transformerXUseCase
                    .decomposeApplicable(
                            context.env.database, TransformationStrategyYesAdapter(), context.env.currentParams)
            result.add(RestTransformApplicability(transformerName,
                    composeApplicable.ok, computeApplicabilityMessage(composeApplicable.message, composeApplicable.tranformerParmeters.toMutableMap()),
                    decomposeApplicable.ok, computeApplicabilityMessage(decomposeApplicable.message, decomposeApplicable.tranformerParmeters.toMutableMap())
            ))
        }
        return result
    }
}