package it.unibz.krdb.kprime.domain.actor

import unibz.cs.semint.kprime.domain.Gid
import unibz.cs.semint.kprime.domain.nextGid



data class Actor(
        val id: Long,
        val name:String,
        val role:String,
        val memberOf:String,
        val pass:String,
        val email:String,
        val gid: Gid = nextGid()
)