package it.unibz.krdb.kprime.domain.source

data class Driver(
        val id: String,           // "1"
        val type:String,          // "jdbc"
        val name:String,          // "MYSQL"
        val className:String,     // "org.hsqldb.jdbc.JDBCDriver"
        val driverPattern:String, // ""
        val jarLocation:String)   // "/home/nipe/Temp/hsqldb-2.6.1.jar"