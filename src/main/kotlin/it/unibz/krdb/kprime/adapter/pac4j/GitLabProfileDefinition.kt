package it.unibz.krdb.kprime.adapter.pac4j

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.scribejava.core.model.OAuth2AccessToken
import org.pac4j.oauth.config.OAuth20Configuration
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition

class GitLabProfileDefinition() : OAuth20ProfileDefinition<GitLabProfile, OAuth20Configuration>() {
    override fun getProfileUrl(p0: OAuth2AccessToken?, p1: OAuth20Configuration?): String {
        return "https://gitlab.inf.unibz.it/api/v4/user"
    }

    override fun extractUserProfile(body: String?): GitLabProfile {
        val profile = GitLabProfile()
        if (body != null) {
            val readTree = ObjectMapper().readTree(body)
            profile.addAttribute("id", readTree["id"].asText())
            profile.addAttribute("login", readTree["name"].asText())
            profile.addAttribute("currentUser", readTree["name"].asText())
            profile.addAttribute("avatar_url", readTree["avatar_url"].asText())
        }
        return profile
    }

}