package it.unibz.krdb.kprime.adapter.pac4j.javalin

import io.javalin.http.Context
import org.pac4j.core.context.JEEContext
import org.pac4j.core.context.session.JEESessionStore
import org.pac4j.core.context.session.SessionStore

data class KPJavalinWebContext(val javalinCtx: Context, val session:SessionStore<JEEContext> = JEESessionStore.INSTANCE) :
        JEEContext(javalinCtx.req,javalinCtx.res, session){

    override fun getPath(): String {
        return javalinCtx.path()
    }
}