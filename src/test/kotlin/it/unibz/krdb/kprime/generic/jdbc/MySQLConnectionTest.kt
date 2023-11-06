package it.unibz.krdb.kprime.generic.jdbc

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

class MySQLConnectionTest {

    @Test
    @Ignore
    fun test_first_mysql_connection() {
        // given
        val dataSourceConnection = DataSourceConnection("test","user","password",
            true,true,false)
//        val dataSource = DataSource("mysql", "db_monitoring", "com.mysql.jdbc.Driver",
//            "jdbc:mysql://localhost:3306/db_monitoring", "user", "password")
        val dataSource = DataSource("mysql", "db_monitoring", "com.mysql.cj.jdbc.Driver",
            "jdbc:mysql://localhost:3306/db_monitoring", "user", "password")
        dataSource.connection = dataSourceConnection
        val sqlAdapter = JdbcAdapter()
        // when
        sqlAdapter.query (dataSource, "SELECT * FROM project", JdbcPrinter::printJsonResultList)
        // then

    }
}