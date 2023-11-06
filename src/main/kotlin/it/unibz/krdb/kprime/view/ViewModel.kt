package it.unibz.krdb.kprime.view

import io.javalin.http.Context
import it.unibz.krdb.kprime.adapter.pac4j.javalin.KPJavalinWebContext
import it.unibz.krdb.kprime.domain.setting.SettingService
import org.pac4j.core.profile.CommonProfile
import org.pac4j.core.profile.ProfileManager
import java.util.HashMap


class ViewModel (val settingService: SettingService) {

    fun baseModel(ctx: Context): Map<String, Any?> {
        val model: MutableMap<String, Any?> = HashMap()
        model["msg"] = MessageBundle(RequestUtil.getSessionLocale(ctx))
        model["currentProject"] = settingService.getProjectName() // FIXME get it from CmdContext.env
        model["currentTrace"] = settingService.getTraceName()
        val profiles = getProfiles(ctx)
        if (profiles == null || profiles.isEmpty()) {
            // user,pass login
            model["currentUser"] = RequestUtil.getSessionCurrentUser(ctx)
        } else {
            // social login
            model["currentUser"] = profiles[0].attributes["login"]
            model["avatarUrl"] = profiles[0].attributes["avatar_url"]
        }
        return model
    }

    fun getProfiles(ctx: Context): List<CommonProfile>? {
        return ProfileManager<CommonProfile>(KPJavalinWebContext(ctx)).getAll(true)
    }

}