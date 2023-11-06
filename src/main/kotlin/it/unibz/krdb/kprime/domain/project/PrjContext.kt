package it.unibz.krdb.kprime.domain.project

import it.unibz.krdb.kprime.domain.trace.TraceName
import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.label.Labelled
import unibz.cs.semint.kprime.domain.label.Labeller
import unibz.cs.semint.kprime.domain.nextGid

data class PrjContext(val name: String,
                      val location: String,
                      val description: String = "",
                      val picUrl: String = "",
                      val activeTrace: String = TraceName.NO_TRACE_NAME.value,
                      val activeTermBase:String = "",
                      val gid:Gid= nextGid(),
                      val partOf: String = "",
                      val license: String? = "",
                      val licenseUrl: String? = "",
                      val termsOfServiceUrl: String? = "",
                      val steward: String? = "",
                      val id: Long = 0
): Labelled by Labeller() {

    var labels: String? = null
        get() = labelsAsString()
        set(value) { field = resetLabels(value?:"") }

    companion object {
        const val NO_WORKING_DIR = ""
        val NO_PrjContext = PrjContext(PrjContextName.NO_PROJECT_NAME.value, NO_WORKING_DIR)
    }

    fun isNoProject() = name.isEmpty()

    fun isActive() = activeTermBase.isNotEmpty() && activeTrace.isNotEmpty()

    fun isAbstract() = location.isEmpty()

}