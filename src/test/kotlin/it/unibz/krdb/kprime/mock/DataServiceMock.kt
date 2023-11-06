package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.adapter.DataServiceAdapter
import it.unibz.krdb.kprime.domain.DataService
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.Setting
import it.unibz.krdb.kprime.domain.trace.TraceName
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.ddl.ChangeSet

class DataServiceMock: DataService {

    override fun getDatabases(traceName: String): List<Database> {
        TODO("Not yet implemented")
    }

    override fun getDatabase(projectLocation: PrjContextLocation, traceName: String, traceFileName: String): Result<Database> {
        TODO("Not yet implemented")
    }

    override fun getChangeSets(traceName: String): List<ChangeSet> {
        TODO("Not yet implemented")
    }

    override fun addChangeSet(traceName: String, traceFileName: String, changeSet: ChangeSet, author: String) {
        TODO("Not yet implemented")
    }

    override fun addDatabase(traceName: String, traceFileName: String, database: Database, author: String) {
        TODO("Not yet implemented")
    }

    override fun queryJsonEntity(prjContext: PrjContext, table: Table, database: Database, conditions: String?): String {
        TODO("Not yet implemented")
    }

    override fun queryLinkedEntity(prjContext: PrjContext, table: Table, database: Database, conditions: String?): String {
        TODO("Not yet implemented")
    }

    override fun computeSelectString(table: Table, database: Database, conditions: String?): String {
        TODO("Not yet implemented")
    }

    override fun queryJsonProvenance(table: Table, database: Database, conditions: String?): String {
        TODO("Not yet implemented")
    }

    override fun newBase(): Database {
        TODO("Not yet implemented")
    }

    override fun prettyDatabase(metaDatabase: Database): String {
        TODO("Not yet implemented")
    }

    override fun createBaseProjectFile(setting: Setting) {
        TODO("Not yet implemented")
    }

    override fun prettyChangeSet(changeSet: ChangeSet): String {
        TODO("Not yet implemented")
    }

    override fun databaseFromDatasource(datasource: DataSource): Database {
        TODO("Not yet implemented")
    }

    override fun databaseFromSourceOrNewH2(toMetaSourceName: String): Database {
        TODO("Not yet implemented")
    }

    override fun databaseFromXml(xml: String): Database {
        TODO("Not yet implemented")
    }

    override fun changeSetFromXml(xml: String): ChangeSet {
        TODO("Not yet implemented")
    }

    override fun query(workingDataSource: DataSource, sqlCommand: String): String {
        TODO("Not yet implemented")
    }

    override fun queryFunctionals(workingDataSource: DataSource, database: Database, tableName: String): String {
        TODO("Not yet implemented")
    }

    override fun getTable(database: Database, entityName: String): Result<Table> {
        TODO("Not yet implemented")
    }

    override fun writeDatabaseFileFromDataSource(
        tracesDir: String,
        sourceName: String,
        datasource: DataSource
    ): String {
        TODO("Not yet implemented")
    }

    override fun getDatabaseInfo(
        projectLocation: PrjContextLocation,
        traceName: String,
        traceFileName: String
    ): DataServiceAdapter.DatabaseInfo {
        TODO("Not yet implemented")
    }

    override fun writeDatabase(database: Database, projectLocation: PrjContextLocation, traceName: TraceName) {
        // DO NOTHING
    }

    override fun createSqlFromFileJson(workingDataDir: String, filename: String, datasource: DataSource): Result<String> {
        TODO("Not yet implemented")
    }

    override fun createTableFromFileJson(
        workingDataDir: String,
        filename: String,
        datasource: DataSource
    ): Result<String> {
        TODO("Not yet implemented")
    }

    override fun newDataSourceFromCSV(existingDataSource: DataSource, prjContextLocation: PrjContextLocation): DataSource {
        TODO("Not yet implemented")
    }

    override fun extractDataSource(prjContext: PrjContext, sourceName: String): DataSource? { TODO("Not yet implemented") }
}