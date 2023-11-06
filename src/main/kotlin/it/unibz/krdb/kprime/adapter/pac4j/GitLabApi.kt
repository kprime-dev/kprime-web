package it.unibz.krdb.kprime.adapter.pac4j

import com.github.scribejava.core.builder.api.DefaultApi20

//fun getAccessTokenEndpoint(): String? {
//    return "https://github.com/login/oauth/access_token"
//}
//
//fun getAuthorizationBaseUrl(): String? {
//    return "https://github.com/login/oauth/authorize"
//}

class GitLabApi: DefaultApi20() {

    override fun getAccessTokenEndpoint(): String {
        return "https://gitlab.inf.unibz.it/oauth/token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "https://gitlab.inf.unibz.it/oauth/authorize"
    }

}