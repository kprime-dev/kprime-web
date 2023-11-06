package it.unibz.krdb.kprime.domain.cmd.argument

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


open class TraceCmdArgumentHour(name:String, description:String)
    : TraceCmdArgument<Date>(name,description) {

    override fun computeValidationErrors(value: Any?): List<String> {
        return when(value) {
            is Date -> computeValidationErrors(value)
            else -> emptyList()
        }
    }

    fun computeValidationErrors(value: String): List<String> {
        val errors = ArrayList<String>()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        try {
            LocalDate.parse(value,formatter)
        } catch (e: DateTimeParseException) {
            errors.add("$name requires an hour in format hh:mm:ss.")
        }
        return errors
    }

    // TODO consider TimeZone and Locale.
//        val zone = ZoneId.of("America/Edmonton")
//        val ldate = LocalDate.ofInstant(instant, zone)
    override fun fromString(value: String): Date {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val localDateTime = LocalTime.parse(value,formatter)
        val instant: Instant = localDateTime.atDate(LocalDate.now()).toInstant(ZoneOffset.UTC)
        return  Date.from(instant)
    }

    fun asString(date: Date):String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val instant = date.toInstant()
        val localDateTime = instant.atZone(ZoneId.of("UTC")).toLocalDateTime()
        return formatter.format(localDateTime)
    }
}