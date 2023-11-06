package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.setting.SettingService

class DriverService(val settingService: SettingService,val driverRepositoryBuilder: DriverRepositoryBuilder): DriverServices {

    //private val driversRepo: DriverRepository = DriverFileRepository(settingService)

    override fun readAllInstanceDrivers(): List<Driver> {
        return driverRepositoryBuilder.build(settingService.getInstanceDir()).findAll()
    }

    override fun readAllInstanceDriversPlusH2(): List<Driver> {
        val findAll = readAllInstanceDrivers().toMutableList()
        findAll.add(
            Driver(
                id = "0",
                type = "jdbc",
                name = "h2",
                className = "org.h2.Driver",
                driverPattern = "",
                jarLocation = ""
            )
        )
        return findAll
    }

    override fun writeAllInstanceDrivers(drivers:List<Driver>) {
        return driverRepositoryBuilder.build(settingService.getInstanceDir()).saveAll(drivers)
    }

}