package it.unibz.krdb.kprime.generic.jdbc

import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*


class DuckDBConnectionTest {

    @Test
    fun test_duckdb_connection() {
        Class.forName("org.duckdb.DuckDBDriver");
        val properties = Properties()
        //ro_prop.setProperty("duckdb.read_only", "true");
        val url = "jdbc:duckdb:" // in memory db
        //val url = "jdbc:duckdb:/tmp/my_database" // file db
        val connection: Connection = DriverManager.getConnection(url, properties)

        val stmt = connection.createStatement();
        stmt.execute("DROP TABLE IF EXISTS items")
        stmt.execute("CREATE TABLE items (item VARCHAR, value DECIMAL(10, 2), count INTEGER)")
        stmt.execute("INSERT INTO items VALUES ('jeans', 20.0, 1), ('hammer', 42.2, 2)")

        connection.prepareStatement("INSERT INTO items VALUES (?, ?, ?);").use { p_stmt ->
            p_stmt.setString(1, "chainsaw")
            p_stmt.setDouble(2, 500.0)
            p_stmt.setInt(3, 42)
            p_stmt.execute()
        }

        val query = "SELECT * FROM items"
        val statement: Statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(query)
        while (resultSet.next()) {
            val column1Value = resultSet.getString("item")
            val column2Value = resultSet.getInt("count")
            println("Column1: $column1Value, Column2: $column2Value")
        }
        resultSet.close()
        statement.close()
        connection.close()
    }

}