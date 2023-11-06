package it.unibz.krdb.kprime.domain

interface LoggerService {

    fun debug(text: String)
    fun info(text: String)
    fun critical(text: String)
    fun warning(text: String)

}