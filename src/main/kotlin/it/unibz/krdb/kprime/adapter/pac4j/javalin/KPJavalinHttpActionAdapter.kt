package it.unibz.krdb.kprime.adapter.pac4j.javalin

import io.javalin.http.BadRequestResponse
import io.javalin.http.ForbiddenResponse
import io.javalin.http.UnauthorizedResponse
import org.pac4j.core.context.HttpConstants
import org.pac4j.core.exception.http.HttpAction
import org.pac4j.core.exception.http.WithContentAction
import org.pac4j.core.exception.http.WithLocationAction
import org.pac4j.core.http.adapter.HttpActionAdapter

class KPJavalinHttpActionAdapter: HttpActionAdapter<Void, KPJavalinWebContext> {
    companion object {
        val INSTANCE = KPJavalinHttpActionAdapter()
    }

    override fun adapt(action: HttpAction, context: KPJavalinWebContext): Void? {
        if (action is WithContentAction) {
            context.javalinCtx.status(action.code)
            context.javalinCtx.result((action as WithContentAction).content)
        }
         else if (action is WithLocationAction) {
            context.javalinCtx.redirect((action as WithLocationAction).location, action.code)
        } else when(action.code) {
            HttpConstants.UNAUTHORIZED -> throw UnauthorizedResponse()
            HttpConstants.FORBIDDEN -> throw ForbiddenResponse()
            HttpConstants.BAD_REQUEST -> throw BadRequestResponse()
            else -> context.javalinCtx.status(action.getCode())
        }
        return null
    }

}