package it.unibz.krdb.kprime.view.controller

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.user.User
import it.unibz.krdb.kprime.adapter.pac4j.javalin.KPLogoutHandler
import it.unibz.krdb.kprime.domain.user.UserService
import it.unibz.krdb.kprime.view.Path
import it.unibz.krdb.kprime.view.RequestUtil.getQueryLoginRedirect
import it.unibz.krdb.kprime.view.RequestUtil.getQueryPassword
import it.unibz.krdb.kprime.view.RequestUtil.getQueryUsername
import it.unibz.krdb.kprime.view.RequestUtil.removeSessionAttrLoggedOut
import it.unibz.krdb.kprime.view.RequestUtil.removeSessionAttrLoginRedirect
import it.unibz.krdb.kprime.view.ViewModel
import org.pac4j.core.config.Config

class LoginController(val userService: UserService, val viewModel: ViewModel) {

    @Suppress("UNCHECKED_CAST")
    var serveLoginPage = Handler { ctx: Context ->
        val model: MutableMap<String, Any> = viewModel.baseModel(ctx) as MutableMap<String, Any>
        model["loggedOut"] = removeSessionAttrLoggedOut(ctx)
        model["loginRedirect"] = removeSessionAttrLoginRedirect(ctx) ?: ""
        model["hasGitHubKey"] = hasEnvKey("KPRIME_GITHUB_KEY")
        model["hasGitLabKey"] = hasEnvKey("KPRIME_GITLAB_KEY")
        model["authenticationFailed"] = false
        model["authenticationSucceeded"] = false
        model["currentUser"] = ""
        ctx.render(Path.Template.LOGIN, model)
    }

    var handleLoginPost = Handler { ctx: Context ->
        val model: MutableMap<String, Any?> = viewModel.baseModel(ctx) as MutableMap<String, Any?>
        if (!userService.authenticate(getQueryUsername(ctx), getQueryPassword(ctx))) {
            model["loggedOut"] = removeSessionAttrLoggedOut(ctx)
            model["loginRedirect"] = removeSessionAttrLoginRedirect(ctx) ?: ""
            model["hasGitHubKey"] = hasEnvKey("KPRIME_GITHUB_KEY")
            model["hasGitLabKey"] = hasEnvKey("KPRIME_GITLAB_KEY")
            model["authenticationFailed"] = true
            model["authenticationSucceeded"] = false
            model["currentUser"] = ""
            ctx.render(Path.Template.LOGIN, model)
        } else {
            ctx.sessionAttribute("currentUser", getQueryUsername(ctx))
            model["authenticationFailed"] = false
            model["authenticationSucceeded"] = true
            model["currentUser"] = getQueryUsername(ctx)
            if (getQueryLoginRedirect(ctx) != null) {
                ctx.redirect(getQueryLoginRedirect(ctx)?:Path.Page.INDEX)
            }
            ctx.redirect(Path.Page.INDEX)
            //ctx.render(Path.Template.INDEX, model)
        }
    }

    private fun hasEnvKey(key:String) = System.getenv(key) != null && System.getenv(key).isNotEmpty()

    fun handleLogoutPost(ctx: Context,securityConfig:Config) : Handler {
        return Handler {
            localLogoutHandler(securityConfig)
            ctx.sessionAttribute("currentUser", null)
            ctx.sessionAttribute("loggedOut", "true")
            ctx.redirect(Path.Web.LOGIN)
        }
    }

    private fun centralLogoutHandler(config: Config): KPLogoutHandler? {
        var centralLogout = KPLogoutHandler(config,
        defaultUrl = "/",
        logoutUrlPattern = "*",
        localLogout = false,
        centralLogout = true,
        destroySession = true)
        return centralLogout
    }

    private fun localLogoutHandler(config: Config): KPLogoutHandler? {
        val localLogout = KPLogoutHandler(config, "/",
                logoutUrlPattern = "*",
                destroySession = true)
        return localLogout
    }

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    var ensureLoginBeforeViewingUsers = Handler { ctx: Context ->
        if (!ctx.path().startsWith("/login")
            && !ctx.path().startsWith("/myuser")
            && !ctx.path().startsWith("/noteview")
            && !ctx.path().startsWith("/circlemap")
            && !ctx.path().startsWith("/forcetree")
            && !ctx.path().startsWith("/search")
            && !ctx.path().startsWith("/github")
            && !ctx.path().startsWith("/gitlab")
            && !ctx.path().startsWith("/js")
            && !ctx.path().startsWith("/css")
            && !ctx.path().startsWith("/img")
            && !ctx.path().startsWith("/data/")
            && !ctx.path().startsWith("/ldata/")
            && !ctx.path().startsWith("/provdata/")
            && !ctx.path().startsWith("/expert/")
            && !ctx.path().startsWith("/project/")
            && !ctx.path().startsWith("/error/")
            && !ctx.path().startsWith("/cli/")
        ) {

            // FIXME temporaneo salto richiesta di autenticazione
    //        ctx.sessionAttribute("currentUser",users[0].name)
                if ((ctx.sessionAttribute<Any?>(User.CURRENT_USER) == null)
                        ) {
                    ctx.sessionAttribute("loginRedirect", ctx.path())
                    ctx.redirect(Path.Web.LOGIN)
                }
        } else if (ctx.path().startsWith("/github")) {
            if (ctx.sessionAttribute<String>("currentUser")==null) {
                ctx.sessionAttribute("currentUser", User.NO_USER)
            }
        } else if (ctx.path().startsWith("/gitlab")) {
            if (ctx.sessionAttribute<String>("currentUser")==null) {
                ctx.sessionAttribute("currentUser", User.NO_USER)
            }
        } else if (ctx.sessionAttribute<String>(User.CURRENT_USER).equals(User.NO_USER)) {
            ctx.sessionAttribute("loginRedirect", ctx.path())
            ctx.redirect(Path.Web.LOGIN)
        }
    }

    fun starterCentralLogoutHandler(securityConfig: Config): Handler {
        return KPLogoutHandler(securityConfig, "/logout",destroySession = true,logoutUrlPattern = "*"
                ,centralLogout = true)
    }

    fun starterLocalLogoutHandler(securityConfig: Config): KPLogoutHandler {
        return KPLogoutHandler(securityConfig, "/logout",destroySession = true,logoutUrlPattern = "*")
    }

}