package it.unibz.krdb.kprime.adapter.log4j

import it.unibz.krdb.kprime.domain.LoggerService
//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import org.apache.logging.log4j.message.StringFormatterMessageFactory

class Log4JLoggerService : LoggerService{

    //val logger: Logger = LogManager.getLogger(StringFormatterMessageFactory.INSTANCE)

    override fun debug(text: String) {
        //logger.debug(text)
        println("DEBUG:[$text]")
    }

    override fun info(text: String) {
        //logger.info(text)
        println("INFO:[$text]")
    }

    override fun critical(text: String) {
        //logger.fatal(text)
        println("FATAL:[$text]")
    }

    override fun warning(text: String) {
        //logger.warn(text)
        println("WARNING:[$text]")
    }
}