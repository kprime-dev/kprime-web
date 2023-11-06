package it.unibz.krdb.kprime.domain.user

import unibz.cs.semint.kprime.domain.label.Labelled
import unibz.cs.semint.kprime.domain.label.Labeller

data class User (
        val id: String,
        val name:String,
        val role:String,
        val memberOf:String,
        val pass:String,
        val email:String): Labelled by Labeller() {

    enum class ROLE { ANONYMOUS, ADMIN, AUTHOR, READER }

    companion object {
        const val CURRENT_USER = "currentUser"
        const val NO_USER = "no-user"
        const val LOGIN = "login"
    }
}