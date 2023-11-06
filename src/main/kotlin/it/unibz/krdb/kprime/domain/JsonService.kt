package it.unibz.krdb.kprime.domain

interface JsonService {
    fun toJson(obj: Any): String
}