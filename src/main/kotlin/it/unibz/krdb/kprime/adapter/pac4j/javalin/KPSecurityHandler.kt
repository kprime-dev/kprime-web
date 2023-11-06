package it.unibz.krdb.kprime.adapter.pac4j.javalin

import io.javalin.http.Context
import io.javalin.http.Handler
import it.unibz.krdb.kprime.domain.user.User
import org.pac4j.core.config.Config
import org.pac4j.core.context.JEEContext
import org.pac4j.core.context.session.JEESessionStore
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.engine.*
import org.pac4j.core.profile.UserProfile
import org.pac4j.core.util.FindBest

@Suppress("UNCHECKED_CAST")
data class KPSecurityHandler(
        val securityConfig : Config,
        val clientNames : String,
        val authorizerNames: String? = null,
        val matchers: String? = null,
        val multiProfile : Boolean? = null
) : Handler {

    lateinit var securityLogic: SecurityLogic<Any, KPJavalinWebContext>
    val AUTH_GRANTED = "AUTH_GRANTED"

    override fun handle(ctx: Context) {
        val bestSessionStore = FindBest.sessionStore(null, securityConfig, JEESessionStore.INSTANCE)
        val bestAdapter = FindBest.httpActionAdapter(null, securityConfig, KPJavalinHttpActionAdapter.INSTANCE)
        //val bestLogic = FindBest.securityLogic(securityLogic, securityConfig, DefaultSecurityLogic.INSTANCE)
        val bestLogic = DefaultSecurityLogic.INSTANCE
        val context = KPJavalinWebContext(ctx, bestSessionStore as SessionStore<JEEContext>)
        ctx.redirect("/index")
        bestLogic.perform(
                context,
                this.securityConfig,
                SecurityGrantedAccessAdapter<Any, KPJavalinWebContext> { webContext: KPJavalinWebContext?, profiles: Collection<UserProfile?>?, _: Array<Any?>? ->
                    val loginUser = profiles!!.first()!!.getAttribute("login")
                    if (loginUser == User.NO_USER) throw IllegalAccessError()
                    webContext!!.javalinCtx.sessionAttribute("currentUser", loginUser)
                    AUTH_GRANTED }, bestAdapter,
                this.clientNames,
                this.authorizerNames,
                matchers,
                multiProfile
        )
    }
}