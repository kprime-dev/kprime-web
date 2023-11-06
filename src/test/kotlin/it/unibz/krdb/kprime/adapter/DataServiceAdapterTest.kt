package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.adapter.jackson.file.DriverFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.PrjContextFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.SettingFileRepository
import it.unibz.krdb.kprime.adapter.jackson.file.SourceFileRepository
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.source.SourceService
import it.unibz.krdb.kprime.domain.trace.TraceName
import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import java.io.File
import java.io.FileNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataServiceAdapterTest {

    @Test(expected = FileNotFoundException::class)
    fun test_file_not_found_database() {
        // given
        val settingService = SettingService(settingRepo = SettingFileRepository(""))
        val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
        val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(),DriverFileRepository())
        val rdfService = RdfServiceAdapter(settingService)
        val dataServiceAdapter = DataServiceAdapter(settingService, sourceService, rdfService)
        // when
        val result = dataServiceAdapter.getDatabase(PrjContextLocation("xyz"),"abc","123")
        // then
        assertTrue(result.isFailure)
        result.getOrThrow()
    }

    @Test
    fun test_file_found_database() {
        // given
        val settingService = SettingService(settingRepo = SettingFileRepository(""))
        val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
        val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(),DriverFileRepository())
        val rdfService = RdfServiceAdapter(settingService)
        val dataServiceAdapter = DataServiceAdapter(settingService, sourceService, rdfService)
        // when
        val dbpath = this.javaClass.getResource("/").path
        val db = dataServiceAdapter.getDatabase(PrjContextLocation(dbpath),"db","base").getOrThrow()
        // then
        assertEquals("xyz",db?.id)
    }

    @Test
    @Ignore
    fun test_local_file_database_success() {
        val settingService = SettingService(settingRepo = SettingFileRepository(""))
        val prjContextService = PrjContextService(settingService, PrjContextFileRepository())
        val sourceService = SourceService(settingService, prjContextService, SourceFileRepository(),DriverFileRepository())
        val rdfService = RdfServiceAdapter(settingService)
        val dataServiceAdapter = DataServiceAdapter(settingService, sourceService, rdfService)
        // when
        val db = dataServiceAdapter.getDatabase(PrjContextLocation("/home/nipe/Workspaces/semint-kprime-cases/h2sakila/"),"root","base").getOrThrow()
        // then
        assertEquals("82587d0d-ef31-48d9-9b72-2c671b076acf",db?.id)
        assertEquals("ACTOR",dataServiceAdapter.getTable(db,"ACTOR").getOrNull()?.name)

    }

    @Test
    @Ignore
    fun test_write_database() {
        // given
        val database = Database()
        val settingService = SettingService(settingRepo = SettingFileRepository("."))
        val rdfService = RdfServiceAdapter(settingService)
        val sourceService = SourceService(
            settingService,
            PrjContextService(settingService, PrjContextFileRepository()),
            SourceFileRepository(),
            DriverFileRepository()
        )
        val dataService = DataServiceAdapter(settingService, sourceService, rdfService)
        val projectLocation = PrjContextLocation(
            this.javaClass.getResource("/").path
        )
        val traceName = TraceName("totrash/")
        // when
        dataService.writeDatabase(database, projectLocation  ,  TraceName("traces/"+traceName.value))
        // then
        assertTrue (File(projectLocation.value+traceName.value+"base_db.xml").exists())
    }

    @Test
    fun test_json_flatted_sqlize() {
        // given
        val flatten = "{person.data={" +
                "person.data.fname=Text, " +
                "person.data.lname=Text, " +
                "person.data.age=Double, " +
                "person.data.wife.fname=Text, " +
                "person.data.salary=Text, " +
                "person.data.spouse.fname=Text}, " +
                "person.data.phone={" +
                "person.data.phone.colour=Text, " +
                "person.data.phone.dnum=Text, " +
                "person.data.phone.loc=Text}}"
        // when
        val sqls = DataServiceAdapter.fromFlattenToSql(flatten)
        // then
        assertEquals("[CREATE TABLE person_data " +
                "(fname VARCHAR(64),lname VARCHAR(64),age NUMERIC,wife_fname VARCHAR(64),salary VARCHAR(64),spouse_fname VARCHAR(64)), " +
                "CREATE TABLE person_data_phone " +
                "(colour VARCHAR(64),dnum VARCHAR(64),loc VARCHAR(64))]"
            ,sqls.toString())
    }

    @Test
    fun test_toSqlColumn() {
        // given
        val colToken = "person.data.wife.fname=Text"
        // when
        val result = DataServiceAdapter.toSqlColumn("person.data.",colToken)
        // then
        assertEquals("wife_fname VARCHAR(64)",result)

    }

    @Test
    fun test_toSqlLine() {
        // given
        val line = "{person.data={person.data.fname=Text, person.data.lname=Text, person.data.age=Double, person.data.wife.fname=Text, person.data.salary=Text, person.data.spouse.fname=Text,"
        // when
        val result = DataServiceAdapter.toSqlLine(line)
        // then
        assertEquals("CREATE TABLE person_data (fname VARCHAR(64),lname VARCHAR(64),age NUMERIC,wife_fname VARCHAR(64),salary VARCHAR(64),spouse_fname VARCHAR(64))",result)
    }

    @Test
    fun test_fromFlattenToSql() {
        // given
        val flatten= """
            {person.json.data={person.json.data.fname=Text
             person.json.data.lname=Text
             person.json.data.age=Double
             person.json.data.wife.fname=Text
             person.json.data.salary=Text
             person.json.data.spouse.fname=Text}
             person.json.data.phone={person.json.data.phone.colour=Text
             person.json.data.phone.dnum=Text
             person.json.data.phone.loc=Text}}            
        """.trimIndent()
        // when
        val sql = DataServiceAdapter.fromFlattenToSql(flatten)
        // then
        assertEquals("[CREATE TABLE person_json_data " +
                "(fname VARCHAR(64),lname VARCHAR(64),age NUMERIC,wife_fname VARCHAR(64),salary VARCHAR(64),spouse_fname VARCHAR(64)), " +
                "CREATE TABLE person_json_data_phone " +
                "(colour VARCHAR(64),dnum VARCHAR(64),loc VARCHAR(64))]",sql.toString())
    }

    @Test
    fun test_fromFlattenToTable() {
        // given
        val flatten= """
            {person.json.data={person.json.data.fname=Text
             person.json.data.lname=Text
             person.json.data.age=Double
             person.json.data.wife.fname=Text
             person.json.data.salary=Text
             person.json.data.spouse.fname=Text}
             person.json.data.phone={person.json.data.phone.colour=Text
             person.json.data.phone.dnum=Text
             person.json.data.phone.loc=Text}}            
        """.trimIndent()
        // when
        val tableLine = DataServiceAdapter.fromFlattenToTable(flatten)
        // then
        assertEquals("[" +
                "create-table person_json_data:  text:fname, text:lname, numeric:age, text:wife_fname, text:salary, text:spouse_fname, " +
                "create-table person_json_data_phone:  text:colour, text:dnum, text:loc" +
                "]",tableLine.toString())
    }

    @Test
    fun test_fromFlattenToTable2() {
        // given
        val flatten= """
            {person.json.data={person.json.data.fname=Text}}
        """.trimIndent()
        // when
        val tableLine = DataServiceAdapter.fromFlattenToTable(flatten)
        // then
        assertEquals("[" +
                "create-table person_json_data:  text:fname" +
                "]",tableLine.toString())
    }


    @Test
    fun test_from_json_to_table() {
        val json = """
            {
                "name":"Giovanni",
                "surname":"Rossi"
            }
        """.trimIndent()
        // when
        val fromJsonToTable = DataServiceAdapter.fromJsonToTable(json, "person.json")
            .joinToString(System.lineSeparator())
        // then
        assertEquals("create-table person_json:  text:name, text:surname",fromJsonToTable)
    }


    @Test // errato
    fun test_from_json_to_table2() {
        val json = """
            {
                "name":"Giovanni",
                "surname":"Rossi",
                "address":[{
                    "via":"4 stagioni",
                    "paese":"Trento",
                    "stato":"Italia"
                }]
            }
        """.trimIndent()
        // when
        val fromJsonToTable = DataServiceAdapter.fromJsonToTable(json, "person.json")
            .joinToString(System.lineSeparator())
        // then
        assertEquals("""
            create-table person_json:  text:name, text:surname
            create-table person_json_address:  text:via, text:paese, text:stato
        """.trimIndent(),fromJsonToTable)
    }

    @Test
    fun test_from_json_to_table3() {
        val json = """
            {
                "name":"Giovanni",
                "surname":"Rossi",
                "address":{
                    "via":"4 stagioni",
                    "paese":"Trento",
                    "stato":"Italia"
                }
            }
        """.trimIndent()
        // when
        val fromJsonToTable = DataServiceAdapter.fromJsonToTable(json, "person.json")
            .joinToString(System.lineSeparator())
        // then
        assertEquals("""
            create-table person_json:  text:name, text:surname, text:address_via, text:address_paese, text:address_stato
            create-table person_json_address:  text:t, text:text, text:via, text:paese, text:stato
        """.trimIndent(),fromJsonToTable)
    }

}