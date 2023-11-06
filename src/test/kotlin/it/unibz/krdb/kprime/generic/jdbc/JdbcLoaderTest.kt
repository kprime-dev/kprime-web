package it.unibz.krdb.kprime.generic.jdbc

import org.junit.Ignore
import org.junit.Test
import java.net.URL
import java.net.URLClassLoader
import java.sql.*
import java.util.*
import java.util.logging.Logger


class JdbcLoaderTest {

    internal class DriverShim(d: Driver) : Driver {
        private val driver: Driver

        init {
            driver = d
        }

        @Throws(SQLException::class)
        override fun acceptsURL(u: String?): Boolean {
            return driver.acceptsURL(u)
        }

        @Throws(SQLException::class)
        override fun connect(u: String?, p: Properties?): Connection {
            return driver.connect(u, p)
        }

        @Throws(SQLException::class)
        override fun getPropertyInfo(u: String?, p: Properties?): Array<DriverPropertyInfo> {
            return driver.getPropertyInfo(u, p)
        }

        override fun getMajorVersion(): Int {
            return driver.majorVersion
        }

        override fun getMinorVersion(): Int {
            return driver.minorVersion
        }

        override fun jdbcCompliant(): Boolean {
            return driver.jdbcCompliant()
        }

        override fun getParentLogger(): Logger {
            return driver.parentLogger
        }
    }

    @Test
    @Ignore
    fun test_psql_jdbc_dynamic_loading() {
        val u = URL("jar:file:/home/nipe/Temp/postgresql-42.2.8.jar!/")
        val classname = "org.postgresql.Driver"
        val ucl = URLClassLoader(arrayOf(u) )
        val d = Class.forName(classname, true, ucl).newInstance() as Driver
        DriverManager.registerDriver(DriverShim(d))
        val c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sakila", "npedot", "pass")
        println(c.metaData.databaseMajorVersion)
    }

    @Test
    @Ignore
    fun test_hsqldb_jdbc_dynamic_loading() {
        val u = URL("jar:file:/home/nipe/Temp/hsqldb-2.6.1.jar!/")
        val classname = "org.hsqldb.jdbc.JDBCDriver"
        val ucl = URLClassLoader(arrayOf(u) )
        val d = Class.forName(classname, true, ucl).newInstance() as Driver
        DriverManager.registerDriver(DriverShim(d))
        val c = DriverManager.getConnection("jdbc:hsqldb:ram://localhost/testdb", "SA", "")
        println(c.metaData.databaseMajorVersion)
        println(c.metaData.databaseMinorVersion)
    }

    @Test
    @Ignore
    fun test_mysql_jdbc_dynamic_loading() {
        val u = URL("jar:file:/home/nipe/.m2/repository/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar!/")
        val classname = "com.mysql.jdbc.Driver"
        val ucl = URLClassLoader(arrayOf(u) )
        val d = Class.forName(classname, true, ucl).newInstance() as Driver
        DriverManager.registerDriver(DriverShim(d))
        println(d.majorVersion)
        println(d.minorVersion)
    }

}