package it.unibz.krdb.kprime.adapter.jackson

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import it.unibz.krdb.kprime.domain.project.PrjContext
import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.label.Labelled
import unibz.cs.semint.kprime.domain.label.Labeller
import unibz.cs.semint.kprime.domain.nextGid

data class JacksonProject(var name: String,
                   var location: String,
                   var description: String = "",
                   var picUrl: String = "",
                   var activeTrace: String = "",
                   var activeTermBase:String = "",
                   var gid:Gid= nextGid(),
                   var partOf: String = "",
                   var license: String? = "",
                   var licenseUrl: String? = "",
                   val termsOfServiceUrl: String? = "",
                   val steward: String? = "",
                   val id: Long = 0
): Labelled by Labeller() {

    @JacksonXmlProperty(isAttribute = true)
    var labels: String? = null
        get() = labelsAsString()
        set(value) { field = resetLabels(value?:"") }

    @JsonIgnore
    fun isNoProject() = true

    @JsonIgnore
    fun isActive() = true

    @JsonIgnore
    fun isAbstract() = true

    companion object {
        @JsonIgnore
        val NO_WORKING_DIR = ""
        @JsonIgnore
        val NO_PROJECT = JacksonProject("", NO_WORKING_DIR)

        fun from(prjContext: PrjContext): JacksonProject {
            val jacksonProject = JacksonProject(
                    prjContext.name,
                    prjContext.location,
                    prjContext.description,
                    prjContext.picUrl,
                    prjContext.activeTrace,
                    prjContext.activeTermBase,
                    prjContext.gid,
                    prjContext.partOf,
                    prjContext.license,
                    prjContext.licenseUrl,
                    prjContext.termsOfServiceUrl,
                    prjContext.steward,
                    prjContext.id
            )
            jacksonProject.labels = prjContext.labels
            return jacksonProject
        }

        fun toProject(jacksonProject: JacksonProject): PrjContext {
            val prjContext = PrjContext(jacksonProject.name,
                    jacksonProject.location,
                    jacksonProject.description,
                    jacksonProject.picUrl,
                    jacksonProject.activeTrace,
                    jacksonProject.activeTermBase,
                    jacksonProject.gid,
                    jacksonProject.partOf,
                    jacksonProject.license,
                    jacksonProject.licenseUrl,
                    jacksonProject.termsOfServiceUrl,
                    jacksonProject.steward,
                    jacksonProject.id
            )
            prjContext.labels = jacksonProject.labels
            return prjContext
        }
    }

}