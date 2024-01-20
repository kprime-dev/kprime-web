package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.setting.SettingService
import java.sql.DriverManager
import kotlin.streams.toList

class DriverService(val settingService: SettingService,val driverRepositoryBuilder: DriverRepositoryBuilder): DriverServices {

    override fun readAllInstanceDrivers(): List<Driver> {
        val managedDrivers = DriverManager.drivers().toList()
            .mapIndexed { index, it ->
                Driver(
                    "${index + 1}",
                    type = "jdbc",
                    name = it.javaClass.canonicalName + " version.${it.majorVersion}.${it.minorVersion}",
                    className = it.javaClass.canonicalName,
                    "",
                    ""
                )
            }
            .toList()
        return managedDrivers
    }

}