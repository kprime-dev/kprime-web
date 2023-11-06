package it.unibz.krdb.kprime.support

import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class TimeSupport {

    companion object {

        fun toLocalDate(date:Date): LocalDate? {
            return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate()
        }

        fun format(date: Date):String {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return formatter.format(toLocalDate(date))
        }

        fun toDate(value: String): Date {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val ldate = LocalDate.parse(value,formatter)
//        val zone = ZoneId.of("America/Edmonton")
//        val ldate = LocalDate.ofInstant(instant, zone)
            return Date.from(ldate.atStartOfDay(ZoneOffset.UTC).toInstant())
        }

        fun distance(date1: Date?, date2: Date?): Period {
            if (date1==null || date2==null) return Period.of(0,0,0)
            val firstDate = toLocalDate(date1)
            val secondDate = toLocalDate(date2)
            return Period.between(firstDate, secondDate)
        }

    }
}