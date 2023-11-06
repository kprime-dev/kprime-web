package it.unibz.krdb.kprime.generic.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibz.krdb.kprime.adapter.jackson.JacksonFlattener
import it.unibz.krdb.kprime.adapter.jackson.JsonTable
import org.junit.Test
import kotlin.test.assertEquals


class JacksonFlattenerTest {

    @Test
    fun test_flatten_json() {
        val mapper = ObjectMapper()
        val jsonString = """
    {
        "name": "John Doe",
        "age": 30,
        "address": {
            "street": "123 Main Street",
            "city": "Anytown",
            "state": "CA",
            "zip": "91234"
        },
        "hobbies": [
            {
                "name": "Reading",
                "level": "Intermediate"
            },
            {
                "name": "Writing",
                "level": "Advanced"
            }
        ]
    }
    """.trimIndent()

        // Parse the JSON input into an object
        val person = mapper.readValue(jsonString, Any::class.java)

        // Flatten the properties of the object
        val flattenedPerson = mapper.convertValue(person, Map::class.java)

        // Print the flattened object as a table
        assertEquals("name|age|address|hobbies",flattenedPerson.keys.joinToString(separator = "|"))
        assertEquals("John Doe|30|{street=123 Main Street, city=Anytown, state=CA, zip=91234}|[{name=Reading, level=Intermediate}, {name=Writing, level=Advanced}]",flattenedPerson.values.joinToString(separator = "|"))
    }


    @Test
    fun test_tree_person_hobbies() {
        val mapper = ObjectMapper()
        val jsonString = """
            [
    {
        "name": "John Doe",
        "age": 30,
        "address": {
            "street": "123 Main Street",
            "city": "Anytown",
            "state": "CA",
            "zip": "91234"
        },
        "hobbies": [
            {
                "name": "Reading",
                "level": "Intermediate"
            },
            {
                "name": "Writing",
                "level": "Advanced"
            }
        ]
    },
    {
        "name": "Mary Doe",
        "age": 33,
        "address": {
            "street": "123 Mary Street",
            "city": "Anytown",
            "state": "CA",
            "zip": "91234"
        },
        "hobbies": [
            {
                "name": "Reading",
                "level": "Interm"
            },
            {
                "name": "Writing",
                "level": "Mid"
            }
        ]
    }
    ]    
    """.trimIndent()
        val person = mapper.readTree(jsonString)
        val tables = mutableMapOf<String, JsonTable>()
        val table0 = mutableMapOf<String,Any>()
        JacksonFlattener.computeInstances(person,"person", 0, tables, table0)
        assertEquals("{person1={person1.name1=John Doe, person1.age1=30.0, " +
                "person1.address1.street1=123 Main Street, person1.address1.city1=Anytown, " +
                "person1.address1.state1=CA, person1.address1.zip1=91234}, " +
                "person1.hobbies1={person1.hobbies1.name1=Reading, " +
                "person1.hobbies1.level1=Intermediate}, " +
                "person1.hobbies2={person1.hobbies2.name2=Writing, " +
                "person1.hobbies2.level2=Advanced}, " +
                "person2={person2.name2=Mary Doe, person2.age2=33.0, " +
                "person2.address2.street2=123 Mary Street, " +
                "person2.address2.city2=Anytown, " +
                "person2.address2.state2=CA, " +
                "person2.address2.zip2=91234}, " +
                "person2.hobbies1={person2.hobbies1.name1=Reading, person2.hobbies1.level1=Interm}, " +
                "person2.hobbies2={person2.hobbies2.name2=Writing, " +
                "person2.hobbies2.level2=Mid}}",tables.toString())
    }

    @Test
    fun test_tree_person_hobbies_schema() {
        val mapper = ObjectMapper()
        val jsonString = """
            [
    {
        "name": "John Doe",
        "age": 30,
        "address": {
            "street": "123 Main Street",
            "city": "Anytown",
            "state": "CA",
            "zip": "91234"
        },
        "hobbies": [
            {
                "name": "Reading",
                "level": "Intermediate"
            },
            {
                "name": "Writing",
                "level": "Advanced"
            }
        ]
    },
    {
        "name": "Mary Doe",
        "age": 33,
        "address": {
            "street": "123 Mary Street",
            "city": "Anytown",
            "state": "CA",
            "zip": "91234"
        },
        "hobbies": [
            {
                "name": "Reading",
                "level": "Interm"
            },
            {
                "name": "Writing",
                "level": "Mid"
            }
        ]
    }
    ]    
    """.trimIndent()
        val person = mapper.readTree(jsonString)
        val tables = mutableMapOf<String, JsonTable>()
        val table0 = mutableMapOf<String,Any>()
        JacksonFlattener.computeSchema(person,"person", 0, tables, table0)
        assertEquals("{person={" +
                "person.name=Text, " +
                "person.age=Double, " +
                "person.address.street=Text, " +
                "person.address.city=Text, " +
                "person.address.state=Text, " +
                "person.address.zip=Text}, " +
                "person.address={" +
                "person.name=Text, " +
                "person.age=Double, " +
                "person.address.street=Text, " +
                "person.address.city=Text, " +
                "person.address.state=Text, " +
                "person.address.zip=Text}, " +
                "person.hobbies={" +
                "person.hobbies.name=Text, " +
                "person.hobbies.level=Text}}", tables.toString())
    }

    @Test
    fun test_tree_person_phones() {
        // given
        val json = """
            { "collection": "person",
                "data" : [
                    { "fname": "John", "lname": "Smith", "age": 25,
                      "wife": { "fname" : "Mary" },
                      "phone": [
                          {"colour": "red", "dnum": "212 555-1234"}
                        ] 
                    } ,
                    { "fname": "Mary", "lname": "Jones", "salary": "${'$'}150,000 (CAD)",
                      "spouse": { "fname": "John" },
                      "phone": [
                          {"loc": "home", "dnum": "212 555-1234"},
                          {"loc": "work", "dnum": "212 666-4567"}
                        ] 
                    }
                ] 
            }
        """.trimIndent()
        // when
        val jsonNode = ObjectMapper().readTree(json)
        val tables = mutableMapOf<String, JsonTable>()
        val table0 = mutableMapOf<String, Any>()
        JacksonFlattener.computeInstances(jsonNode, "person", 0, tables, table0)
        // then
        assertEquals(
            "{person0.data1={" +
                    "person0.data1.fname1=John, " +
                    "person0.data1.lname1=Smith, " +
                    "person0.data1.age1=25.0, " +
                    "person0.data1.wife1.fname1=Mary}, " +
                    "person0.data1.phone1={" +
                    "person0.data1.phone1.colour1=red, " +
                    "person0.data1.phone1.dnum1=212 555-1234}, " +
                    "person0.data2={" +
                    "person0.data2.fname2=Mary, " +
                    "person0.data2.lname2=Jones, " +
                    "person0.data2.salary2=\$150,000 (CAD), " +
                    "person0.data2.spouse2.fname2=John}, " +
                    "person0.data2.phone1={" +
                    "person0.data2.phone1.loc1=home, " +
                    "person0.data2.phone1.dnum1=212 555-1234}, " +
                    "person0.data2.phone2={" +
                    "person0.data2.phone2.loc2=work, " +
                    "person0.data2.phone2.dnum2=212 666-4567}}", tables.toString()
        )
    }

    @Test
    fun test_tree_person_phones_schema() {
        // given
        val mapper = ObjectMapper()
        val json = """
            { "collection": "person",
                "data" : [
                    { "fname": "John", "lname": "Smith", "age": 25,
                      "wife": { "fname" : "Mary" },
                      "phone": [
                          {"colour": "red", "dnum": "212 555-1234"}
                        ] 
                    } ,
                    { "fname": "Mary", "lname": "Jones", "salary": "${'$'}150,000 (CAD)",
                      "spouse": { "fname": "John" },
                      "phone": [
                          {"loc": "home", "dnum": "212 555-1234"},
                          {"loc": "work", "dnum": "212 666-4567"}
                        ] 
                    }
                ] 
            }
        """.trimIndent()
        // when
        val person = mapper.readTree(json)
        val tables = mutableMapOf<String, JsonTable>()
        val table0 = mutableMapOf<String,Any>()
        JacksonFlattener.computeSchema(person,"person", 0, tables, table0)
        //println(tables.toString())
        // then
        assertEquals("{person=" +
                "{person.collection=Text}, " +
                "person.data={person.data.fname=Text, " +
                "person.data.lname=Text, " +
                "person.data.age=Double, " +
                "person.data.wife.fname=Text, " +
                "person.data.salary=Text, " +
                "person.data.spouse.fname=Text}, " +
                "person.data.wife={" +
                "person.data.fname=Text, " +
                "person.data.lname=Text, person.data.age=Double, " +
                "person.data.wife.fname=Text, " +
                "person.data.salary=Text, " +
                "person.data.spouse.fname=Text}, " +
                "person.data.phone={" +
                "person.data.phone.colour=Text, " +
                "person.data.phone.dnum=Text, " +
                "person.data.phone.loc=Text}, " +
                "person.data.spouse={" +
                "person.data.fname=Text, " +
                "person.data.lname=Text, " +
                "person.data.age=Double, " +
                "person.data.wife.fname=Text, " +
                "person.data.salary=Text, " +
                "person.data.spouse.fname=Text}}",tables.toString())
    }

    @Test
    fun test_simple_json_flatten() {
        // given
        val json = """
            {   "name": "nico" }
        """.trimIndent()
        // when
        val person = ObjectMapper().readTree(json)
        val tables = mutableMapOf<String, JsonTable>()
        val table0 = mutableMapOf<String,Any>()
        JacksonFlattener.computeSchema(person,"person", 0, tables, table0)
        // then
        assertEquals("{person={person.name=Text}}", tables.toString())
    }
}