package it.unibz.krdb.kprime.adapter.pac4j.javalin

import io.javalin.http.Context
import io.javalin.http.Handler
import org.pac4j.core.config.Config
import org.pac4j.core.context.JEEContext
import org.pac4j.core.context.session.JEESessionStore
import org.pac4j.core.context.session.SessionStore
import org.pac4j.core.engine.CallbackLogic
import org.pac4j.core.engine.DefaultCallbackLogic
import org.pac4j.core.util.FindBest

@Suppress("UNCHECKED_CAST")
data class KPCallbackHandler(
        val securityConfig : Config,
        val defaultUrl : String? = null,
        val saveInSession: Boolean? = true,
        val renewSession: Boolean? = null,
        val multiProfile : Boolean? = null
) : Handler {

    lateinit var callbackLogic: CallbackLogic<Any, KPJavalinWebContext>

    override fun handle(ctx: Context) {
        val bestSessionStore = FindBest.sessionStore(null, securityConfig, JEESessionStore.INSTANCE)
        val bestAdapter = FindBest.httpActionAdapter(null, securityConfig, KPJavalinHttpActionAdapter.INSTANCE)
        //val bestLogic = FindBest.callbackLogic(callbackLogic, securityConfig, DefaultCallbackLogic.INSTANCE)
        val bestLogic = DefaultCallbackLogic.INSTANCE
        val context = KPJavalinWebContext(ctx, bestSessionStore as SessionStore<JEEContext>)
        ctx.redirect("/index")
        //ctx.sessionAttribute("currentUser",context.javalinCtx.attribute("currentUser"))

        bestLogic.perform(context,
                this.securityConfig,
                bestAdapter,
                this.defaultUrl,
                this.saveInSession,
                this.multiProfile,
                this.renewSession,
                "FormClient")

    }
}