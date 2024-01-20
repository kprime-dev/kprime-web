package it.unibz.krdb.kprime.domain.source

interface DriverServices {

    fun readAllInstanceDrivers(): List<Driver>
}