package it.unibz.krdb.kprime.domain.source

interface DriverServices {

    fun readAllInstanceDrivers(): List<Driver>
    fun readAllInstanceDriversPlusH2(): List<Driver>
    fun writeAllInstanceDrivers(drivers:List<Driver>)
}