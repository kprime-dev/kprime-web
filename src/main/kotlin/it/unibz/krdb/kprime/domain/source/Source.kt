package it.unibz.krdb.kprime.domain.source

data class Source(
        val id: String,         // "1"
        val type:String,        // "psql"
        val name:String,        // "sakila"
        val driver:String = "",      // "org.postgresql.Driver"
        val location:String,    // "jdbc:postgresql://localhost:5432/sakila"
        val user:String = "",
        val pass:String = "",
        val license:String? = "",
        val licenseUrl:String? = "",
        val driverUrl:String = "") {

}