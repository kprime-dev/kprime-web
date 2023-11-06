package it.unibz.krdb.kprime.adapter.pac4j

import org.pac4j.oauth.profile.OAuth20Profile

class GitLabProfile(): OAuth20Profile() {
    var name:String = ""
    var avatarUrl:String = ""
}