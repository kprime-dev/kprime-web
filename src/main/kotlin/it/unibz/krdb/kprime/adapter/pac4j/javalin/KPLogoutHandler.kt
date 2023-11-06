package it.unibz.krdb.kprime.adapter.pac4j.javalin

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.view.Path
import org.pac4j.core.config.Config
import org.pac4j.core.context.JEEContext
import org.pac4j.core.context.session.JEESessionStore
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.engine.DefaultLogoutLogic
import org.pac4j.core.engine.LogoutLogic
import org.pac4j.core.util.FindBest

@Suppress("UNCHECKED_CAST")
data class KPLogoutHandler(
        val securityConfig : Config,
        val defaultUrl : String,
        val logoutUrlPattern : String,
        val localLogout : Boolean? = null,
        val destroySession : Boolean? = null,
        val centralLogout : Boolean? = null
) : Handler {

    lateinit var logoutLogic: LogoutLogic<Any, KPJavalinWebContext>

    override fun handle(ctx: Context) {
        val bestSessionStore = FindBest.sessionStore(null, securityConfig, JEESessionStore.INSTANCE)
        val bestAdapter = FindBest.httpActionAdapter(null, securityConfig, KPJavalinHttpActionAdapter.INSTANCE)
        //val bestLogic = FindBest.logoutLogic(logoutLogic, securityConfig, DefaultLogoutLogic.INSTANCE)
        val bestLogic = DefaultLogoutLogic.INSTANCE
        val context = KPJavalinWebContext(ctx, bestSessionStore as SessionStore<JEEContext>)
        ctx.sessionAttribute("currentUser", null)
        ctx.sessionAttribute("loggedOut", "true")
        ctx.redirect(Path.Web.LOGIN)
        bestLogic.perform(context,
                this.securityConfig,
                bestAdapter,
                this.defaultUrl,
                this.logoutUrlPattern,
                this.localLogout,
                this.destroySession,
                this.centralLogout)

    }
}