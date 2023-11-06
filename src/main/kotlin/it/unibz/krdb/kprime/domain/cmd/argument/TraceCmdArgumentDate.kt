package it.unibz.krdb.kprime.domain.cmd.argument

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


open class TraceCmdArgumentDate(name:String, description:String)
    : TraceCmdArgument<Date>(name,description) {

    override fun computeValidationErrors(value: Any?): List<String> {
        return when(value) {
            is Date -> computeValidationErrors(value)
            else -> emptyList()
        }
    }

    fun computeValidationErrors(value: String): List<String> {
        val errors = ArrayList<String>()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            LocalDate.parse(value,formatter)
        } catch (e: DateTimeParseException) {
            errors.add("$name requires a date in format dd-mm-yyyy.")
        }
        return errors
    }

    // TODO consider TimeZone and Locale.
    override fun fromString(value: String): Date {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val ldate = LocalDate.parse(value,formatter)
//        val zone = ZoneId.of("America/Edmonton")
//        val ldate = LocalDate.ofInstant(instant, zone)
        return Date.from(ldate.atStartOfDay(ZoneOffset.UTC).toInstant())
    }

    fun asString(date: Date):String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val localDate = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate()
        return formatter.format(localDate)
    }
}