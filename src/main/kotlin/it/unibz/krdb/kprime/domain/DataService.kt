package it.unibz.krdb.kprime.domain

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.adapter.DataServiceAdapter
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.trace.TraceName
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import java.io.File

interface DataService {

    fun getDatabases(traceName: String): List<Database>

    fun getDatabase(projectLocation: PrjContextLocation, traceName: String, traceFileName: String): Result<Database>

    fun getChangeSets(traceName: String): List<ChangeSet>

    fun addChangeSet(traceName: String, traceFileName: String, changeSet: ChangeSet, author: String ="")

    fun addDatabase(traceName: String, traceFileName: String, database: Database, author: String ="")

    fun queryJsonEntity(prjContext: PrjContext, table: Table, database: Database, conditions: String?): String

    fun queryLinkedEntity(prjContext: PrjContext, table: Table, database: Database, conditions: String?): String

    fun computeSelectString(table: Table, database: Database, conditions: String?): String

    fun queryJsonProvenance(table: Table, database: Database, conditions: String?): String

    fun newBase(): Database

    fun prettyDatabase(metaDatabase: Database): String

    fun createBaseProjectFile(setting: Setting)

    fun prettyChangeSet(changeSet: ChangeSet): String

    fun databaseFromDatasource(datasource: DataSource): Database

    fun databaseFromSourceOrNewH2(toMetaSourceName: String): Database

    fun databaseFromXml(xml: String): Database

    fun changeSetFromXml(xml: String): ChangeSet

    fun query(workingDataSource: DataSource, sqlCommand: String): String

    fun queryFunctionals(workingDataSource: DataSource, database: Database, tableName : String = ""): String

    fun getTable(database: Database, entityName: String): Result<Table>

    fun writeDatabaseFileFromDataSource(tracesDir: String, sourceName: String, datasource: DataSource): String

    fun getDatabaseInfo(projectLocation: PrjContextLocation, traceName: String, traceFileName: String): DataServiceAdapter.DatabaseInfo

    fun writeDatabase(database: Database, projectLocation: PrjContextLocation, traceName: TraceName)

    fun createTableFromFileCsv(
        workingDataDir: String,
        filename: String,
        datasource: DataSource,
        tableName: String
    ) {
        JdbcAdapter().create(datasource, "DROP TABLE IF EXISTS $tableName")
        val sqlcreate = "create TABLE IF NOT EXISTS $tableName AS SELECT * FROM CSVREAD('${workingDataDir + filename}')"
        JdbcAdapter().create(datasource, sqlcreate)
    }

    fun createSqlFromFileJson(
        workingDataDir: String,
        filename: String,
        datasource: DataSource
    ):Result<String>

    fun createTableFromFileSql(
        workingDataDir: String,
        filename: String,
        datasource: DataSource
    ) {
        val sqlcreate = "RUNSCRIPT FROM '${workingDataDir + filename}'"
        JdbcAdapter().create(datasource, sqlcreate)
    }


    fun createTableFromFileJson(workingDataDir: String, filename: String, datasource: DataSource): Result<String>

    fun newDataSourceFromCSV(existingDataSource: DataSource, prjContextLocation: PrjContextLocation): DataSource

    fun extractDataSource(prjContext: PrjContext, sourceName: String): DataSource?
}