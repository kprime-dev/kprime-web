package it.unibz.krdb.kprime.view

import io.javalin.http.Context

object RequestUtil {
    fun getQueryLocale(ctx: Context): String? {
        return ctx.queryParam("locale")
    }

    fun getParamIsbn(ctx: Context): String {
        return ctx.pathParam("isbn")
    }

    fun getQueryUsername(ctx: Context): String? {
        return ctx.formParam("username")
    }

    fun getQueryPassword(ctx: Context): String? {
        return ctx.formParam("password")
    }

    fun getQueryLoginRedirect(ctx: Context): String? {
        return ctx.queryParam("loginRedirect")
    }

    fun getSessionLocale(ctx: Context): String? {
        return ctx.sessionAttribute("locale")
    }

    fun getSessionCurrentUser(ctx: Context): String {
        return ctx.sessionAttribute("currentUser")?: ""
    }

    fun getSessionProjectName(ctx: Context): String? {
        return ctx.sessionAttribute("currentProject")?: ""
    }

    fun removeSessionAttrLoggedOut(ctx: Context): Boolean {
        val loggedOut = ctx.sessionAttribute<String>("loggedOut")
        ctx.sessionAttribute("loggedOut", null)
        return loggedOut != null
    }

    fun removeSessionAttrLoginRedirect(ctx: Context): String? {
        val loginRedirect = ctx.sessionAttribute<String>("loginRedirect")
        ctx.sessionAttribute("loginRedirect", null)
        return loginRedirect
    }
}