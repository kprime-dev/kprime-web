package it.unibz.krdb.kprime.generic.jdbc

import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.sql.*
import java.util.*
import java.util.logging.Logger
import kotlin.streams.toList
import kotlin.test.assertEquals


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
        val uValue = "jar:file:/home/nipe/Dev/drivers/hsqldb-2.6.1.jar!/"
        val u = URL(uValue)
        val classname = "org.hsqldb.jdbc.JDBCDriver"
        val ucl = URLClassLoader(arrayOf(u) )
        val driver = Class.forName(classname, true, ucl).newInstance() as Driver
        println(driver.getPropertyInfo(uValue,null))
        DriverManager.registerDriver(DriverShim(driver))
        val connection = DriverManager.getConnection("jdbc:hsqldb:ram://localhost/testdb", "SA", "")
        println(connection.metaData.databaseMajorVersion)
        println(connection.metaData.databaseMinorVersion)
    }

    @Test
    @Ignore
    fun test_mysql_jdbc_dynamic_loading() {
        assertEquals(4,DriverManager.drivers().toList().size)
//        val u = URL("jar:file:/home/nipe/.m2/repository/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar!/")
//        val classname = "com.mysql.cj.jdbc.Driver"
        val javaFilePath = "/home/nipe/Dev/drivers/mariadb-java-client-3.0.7.jar"
        val u = URL("jar:file:$javaFilePath!/")
        val classname = "org.mariadb.jdbc.Driver"

        val classLoader = ClassLoader.getSystemClassLoader()
        val jarFile = File(javaFilePath)
        val urls = arrayOf(File(javaFilePath).toURI().toURL())
        val urlClassLoader = URLClassLoader(urls,classLoader)

        val method: Method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.setAccessible(true)
        method.invoke(urlClassLoader, jarFile.toURI().toURL());

        val ucl = URLClassLoader(arrayOf(u) )
        val d = Class.forName(classname, true, ucl).newInstance() as Driver
        DriverManager.registerDriver(d)
        println(d.javaClass.name)
        println(d.majorVersion)
        println(d.minorVersion)
        println(d.jdbcCompliant())

        assertEquals(0,DriverManager.drivers().toList().size)
    }

    @Test
    @Ignore
    fun test_classloader_drivers() {
        val drivers = DriverManager.drivers()
        drivers.map { println(it.majorVersion);println(it.minorVersion)}
    }
}