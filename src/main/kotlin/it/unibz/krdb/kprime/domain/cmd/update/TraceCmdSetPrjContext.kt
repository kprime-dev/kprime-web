package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.*
import it.unibz.krdb.kprime.domain.project.PrjContext
import java.util.*

object TraceCmdSetPrjContext : TraceCmd {
    override fun getCmdName(): String {
        return "set-context"
    }

    override fun getCmdDescription(): String {
        return "Set one context attributes. \n e.g. >set-context 1 -url=http://www.ammagamma.com/"
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONTEXT,
            TraceCmd.Topic.CONCEPTUAL).joinToString()
    }

    private enum class ArgNames { CONTEXT_ID, CONTEXT_NAME, location, description, picUrl, partOf, license, licenseUrl, termsOfService, steward }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            //TraceCmdArgumentLong(ArgNames.CONTEXT_ID.name, "Goal ID") required true,
            TraceCmdArgumentText(ArgNames.CONTEXT_NAME.name, "CONTEXT NAME") required true,
            TraceCmdArgumentPath(ArgNames.location.name, "Location") required false,
            TraceCmdArgumentFreeText(ArgNames.description.name, "Description") required false,
            TraceCmdArgumentURL(ArgNames.picUrl.name, "Picture URL") required false,
            TraceCmdArgumentText(ArgNames.partOf.name, "PartOf") required false,
            TraceCmdArgumentFreeText(ArgNames.license.name, "License") required false,
            TraceCmdArgumentURL(ArgNames.licenseUrl.name, "License URL") required false,
            TraceCmdArgumentURL(ArgNames.termsOfService.name, "Terms of Service URL") required false,
            TraceCmdArgumentFreeText(ArgNames.steward.name, "Steward Name") required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val contextNameToSet =  args[ArgNames.CONTEXT_NAME.name] as String
        val location = args[ArgNames.location.name] as String?
        val description = args[ArgNames.description.name] as String?
        val picUrl = args[ArgNames.picUrl.name] as String?
        val partOf = args[ArgNames.partOf.name] as String?
        val license = args[ArgNames.license.name] as String?
        val licenseUrl = args[ArgNames.licenseUrl.name] as String?
        val termsOfService = args[ArgNames.termsOfService.name] as String?
        val steward = args[ArgNames.steward.name] as String?

        var changed = ""
        val prjContext = context.pool.prjContextService.projectByName(contextNameToSet)
            if (prjContext==null)
            {  return TraceCmdResult() failure  "Goal $contextNameToSet to update not found." }
                    else
            { val newProjectContext = PrjContext(
                name = prjContext.name,
                location = if (location!=null) { changed+="location " ; location } else prjContext.location,
                description = if (description!=null) { changed+="description " ; description } else prjContext.description,
                picUrl = if (picUrl!=null) { changed+="picUrl "; picUrl } else prjContext.picUrl,
                partOf = if (partOf!=null) {  changed+="partOf "; partOf } else prjContext.partOf,
                license = if (license!=null) { changed+="license " ; license; } else prjContext.license,
                licenseUrl = if (licenseUrl!=null) { changed+="licenseUrl "; licenseUrl;  } else prjContext.licenseUrl,
                termsOfServiceUrl = if (termsOfService!=null) { changed+="termsOfServiceUrl "; termsOfService } else prjContext.termsOfServiceUrl,
                steward = if (steward!=null) { changed+="steward "; steward } else prjContext.steward)
                if (changed.isNotEmpty())
                    context.pool.prjContextService.update(newProjectContext)
            }
        return if (changed.isNotEmpty()) TraceCmdResult() message "Context $contextNameToSet $changed setted."
        else TraceCmdResult() warning "Nothing Changed in context."
    }

}
