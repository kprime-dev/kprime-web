package it.unibz.krdb.kprime.adapter.pac4j

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.core.config.ConfigFactory
import org.pac4j.core.profile.UserProfile
import org.pac4j.oauth.client.GitHubClient
import org.pac4j.oauth.client.OAuth20Client
import org.pac4j.oauth.config.OAuth20Configuration

class KPrimeSecurityConfigFactory: ConfigFactory {
    override fun build(vararg p0: Any?): Config {

        val host = System.getenv("KPRIME_HOST")
        val github_key = System.getenv("KPRIME_GITHUB_KEY")
        val github_secret = System.getenv("KPRIME_GITHUB_SECRET")
        val gitlab_key = System.getenv("KPRIME_GITLAB_KEY")
        val gitlab_secret = System.getenv("KPRIME_GITLAB_SECRET")
        val callbackUrl = "$host/callback"

        val gitHubClient = GitHubClient(github_key,github_secret)

        val gitLabClient = OAuth20Client()
        val oAuth20Configuration = OAuth20Configuration()
        oAuth20Configuration.api = GitLabApi()
        oAuth20Configuration.profileDefinition = GitLabProfileDefinition()
        gitLabClient.configuration = oAuth20Configuration
        gitLabClient.key = gitlab_key
        gitLabClient.secret = gitlab_secret

        val clients = Clients(callbackUrl, gitHubClient, gitLabClient)
        val config = Config(clients)
        config.addAuthorizer("admin", RequireAnyRoleAuthorizer<UserProfile>("ROLE_ADMIN"))
        return config
    }

}