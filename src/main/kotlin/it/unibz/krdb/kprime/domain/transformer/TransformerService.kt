package it.unibz.krdb.kprime.domain.transformer

import it.unibz.krdb.kprime.domain.setting.SettingService
import java.io.File


// TODO clean File
class TransformerService(private val settingService: SettingService) {


    fun getAllTransformerNames(): List<String> {
        val workingDir  = settingService.getWorkingDir()
        val transformersDir = "${workingDir}transformers/"


        var files = File(transformersDir).listFiles()
        return files?.filter { f -> f.isDirectory }?.map { f -> f.name }?: emptyList<String>()
    }

    fun getTransformerDescriptor(transformerName: String): TransformerDesc {
        return TransformerDesc(transformerName,
                "${transformerName}/compose/${transformerName}_compose_1_paths.properties",
                "${transformerName}/compose/${transformerName}_compose_1_changeset.xml",
                "${transformerName}/decompose/${transformerName}_decompose_1_paths.properties",
                "${transformerName}/decompose/${transformerName}_decompose_1_changeset.xml")
    }

    // { name:'new-transformer', composerMatcher:'', composerTemplate:'',decomposerMatcher:'', decomposerTemplate:'' },
    fun getTransformer(transformerName:String): TransformerDesc {
        val workingDir  = settingService.getWorkingDir()
        val transformerDir = "${workingDir}transformers/${transformerName}"

        val composerMatcherFileName = transformerDir +"/compose/${transformerName}_compose_1_paths.properties"
        var composerMatcher = ""
        if (File(composerMatcherFileName).isFile)
            composerMatcher = File(composerMatcherFileName).readText(Charsets.UTF_8)

        val composerTemplateFileName = transformerDir +"/compose/${transformerName}_compose_1_changeset.xml"
        var composerTemplate = ""
        if (File(composerTemplateFileName).isFile)
            composerTemplate = File(composerTemplateFileName).readText(Charsets.UTF_8)

        var decomposerMatcherFileName = transformerDir +"/decompose/${transformerName}_decompose_1_paths.properties"
        var decomposerMatcher = ""
        if (File(decomposerMatcherFileName).isFile)
            decomposerMatcher = File(decomposerMatcherFileName).readText(Charsets.UTF_8)

        val decomposerTemplateFileName = transformerDir +"/decompose/${transformerName}_decompose_1_changeset.xml"
        var decomposerTemplate = ""
        if (File(decomposerTemplateFileName).isFile)
            decomposerTemplate = File(decomposerTemplateFileName).readText(Charsets.UTF_8)

        return TransformerDesc(transformerName,
                composerMatcher,
                composerTemplate,
                decomposerMatcher,
                decomposerTemplate
            )
    }


    fun deleteTransformer(transformerName: String) {
        val workingDir  = settingService.getWorkingDir()
        val transformerDir = "${workingDir}transformers/${transformerName}"

        val composerMatcherFileName = transformerDir +"/compose/${transformerName}_compose_1_paths.properties"
        File(composerMatcherFileName).delete()

        val composerTemplateFileName = transformerDir +"/compose/${transformerName}_compose_1_changeset.xml"
        File(composerTemplateFileName).delete()

        var decomposerMatcherFileName = transformerDir +"/decompose/${transformerName}_decompose_1_paths.properties"
        File(decomposerMatcherFileName).delete()

        val decomposerTemplateFileName = transformerDir +"/decompose/${transformerName}_decompose_1_changeset.xml"
        File(decomposerTemplateFileName).delete()

        File(transformerDir +"/decompose/").delete()
        File(transformerDir +"/compose/").delete()
        File(transformerDir ).delete()
    }


    fun putTransformer(transformerDesc: TransformerDesc) {
        val workingDir  = settingService.getWorkingDir()
        val transformerName = transformerDesc.name

        if (!File("${workingDir}transformers/").isDirectory) {
            File("${workingDir}transformers/").mkdir()
        }
        val transformerDir = "${workingDir}transformers/${transformerName}"
        if (!File(transformerDir).isDirectory) {
            File(transformerDir).mkdir()
            File(transformerDir+"/compose/").mkdir()
            File(transformerDir+"/decompose/").mkdir()
        }
        val composerMatcherFileName = transformerDir +"/compose/${transformerName}_compose_1_paths.properties"
        File(composerMatcherFileName).writeText(transformerDesc.composeMatcher,Charsets.UTF_8)

        val composerTemplateFileName = transformerDir +"/compose/${transformerName}_compose_1_changeset.xml"
        File(composerTemplateFileName).writeText(transformerDesc.composeTemplate,Charsets.UTF_8)

        var decomposerMatcherFileName = transformerDir +"/decompose/${transformerName}_decompose_1_paths.properties"
        File(decomposerMatcherFileName).writeText(transformerDesc.decomposeMatcher,Charsets.UTF_8)

        val decomposerTemplateFileName = transformerDir +"/decompose/${transformerName}_decompose_1_changeset.xml"
        File(decomposerTemplateFileName).writeText(transformerDesc.decomposeTemplate,Charsets.UTF_8)
    }
}