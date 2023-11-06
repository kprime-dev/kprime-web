package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.adapter.RdfServiceAdapter
import it.unibz.krdb.kprime.adapter.pac4j.javalin.KPJavalinWebContext
import it.unibz.krdb.kprime.domain.DataService
import it.unibz.krdb.kprime.domain.RdfService
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.view.RestError
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.term.LabelField
import it.unibz.krdb.kprime.domain.user.User
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import java.io.File
import javax.servlet.http.HttpServletResponse


class SettingsController(
    settingService: SettingService,
    dataService: DataService,
    rdfService: RdfServiceAdapter,
    prjContextService: PrjContextService
) {

    private fun getProfiles(ctx: Context): List<CommonProfile>? {
        return ProfileManager<CommonProfile>(KPJavalinWebContext(ctx)).getAll(true)
    }

    data class IndexPageModel(
        val currentUser:String,
        val currentProject:String,
        val avatarUrl:String,
        val contextLabels:List<String>,
        val contextLogoUrl:String,
        val contextGID:String)

    var getIndexModel = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val setting = settingService.settingRead(currentUser)
        val profiles = getProfiles(ctx)
        var avatarUrl = ""
        if (!profiles.isNullOrEmpty()) {
            avatarUrl = profiles[0].attributes?.get("avatar_url")?.toString()?:""
        }
        val currentProject = setting.projectName
        val prjContext = prjContextService.projectByName(currentProject)
        val contextGID = prjContext?.gid ?: "no-gid"
        val contextLabels = if (prjContext!=null) {
            val prjContextLocation = PrjContextLocation(prjContext.location)
            val rdfDataDir = RdfService.getPrjContextRdfDataDir(prjContextLocation)
            val rdfIriContext = setting.iriContext+currentProject
            rdfService.findStatements(rdfIriContext, LabelField(contextGID), LabelField("_"), "_", rdfDataDir)
                .fold(onFailure = { it.printStackTrace(); emptyList() },
                    onSuccess = { tripletList -> tripletList
                        .map { triplet -> "${triplet.predicate} ${triplet.cobject}" } }
                )
        } else { emptyList() }
        val contextLogoUrl = prjContext?.picUrl?: "img/context-icon.png"
        ctx.json(IndexPageModel(
            currentUser,
            currentProject,
            avatarUrl,
            contextLabels,
            contextLogoUrl,
            contextGID
        ))
    }

    var getSettings = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val setting = settingService.settingRead(currentUser)
        //val setting2 = Setting(setting.workingDir,settingService.getProjectName(),setting.traceName,setting.databaseName,setting.changesetName,setting.storyName)
        ctx.json(setting)
    }

    var getCidSettings = Handler { ctx: Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val setting = settingService.settingRead(currentUser)
        val setting2 = Setting(setting.workingDir,settingService.getProjectName(),setting.traceName,setting.databaseName,setting.changesetName,setting.storyName)
        ctx.json(setting2)
    }

    var putSettings = Handler { ctx : Context ->
        val currentUser = ctx.sessionAttribute<String>(User.CURRENT_USER)?: User.NO_USER
        val setting = ctx.bodyAsClass<Setting>()
        if (!setting.workingDir.startsWith("/")) {
            ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
            ctx.json(RestError("working dir '${setting.workingDir}' has to start with / .", HttpServletResponse.SC_NOT_ACCEPTABLE))
        } else if (!setting.workingDir.endsWith("/")) {
                ctx.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
                ctx.json(RestError("working dir '${setting.workingDir}' has to end with / .", HttpServletResponse.SC_NOT_ACCEPTABLE))
        } else  {
            println("PUT SETTING: $setting")
            val fileSettings = File(setting.workingDir)
            if (!fileSettings.exists()) {
                fileSettings.mkdir()
            }
            if (fileSettings.list()?.isEmpty() == true) {
                dataService.createBaseProjectFile(setting)
            }
            ctx.status(HttpServletResponse.SC_ACCEPTED)
            println("PUT SETTING ${setting.projectName}")
            settingService.settingWrite(currentUser,setting)
        }
    }


}