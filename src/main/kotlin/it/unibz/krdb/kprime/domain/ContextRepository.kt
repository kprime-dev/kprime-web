package it.unibz.krdb.kprime.domain

data class Contextualized<R>(val contextDir:String, val repo: R)

interface ContextRepository<T> : Repository<T> {
    var repoDir:String
    var repoFileName:String
}