package it.unibz.krdb.kprime.adapter.fact

import it.unibz.krdb.kprime.adapter.fact.FactParser.parseRelationship
import it.unibz.krdb.kprime.adapter.fact.FactParser.parseTriplet
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import kotlin.test.*

/*
prefix dcterms: _
prefix rdfs: http://www.w3.org/2000/01/rdf-schema#
prefix rdf: http://www.w3.org/1999/02/22-rdf-syntax-ns#

dcterms:accessRights a rdf:Property ;
    dcterms:issued "2003-02-15"^^<http://www.w3.org/2001/XMLSchema#date> ;
    dcterms:modified "2008-01-14"^^<http://www.w3.org/2001/XMLSchema#date> ;



add-fact accessRights is-a rdf:Property
add-fact  ... issued "2003-02-15" xmls:date,h2:data
add-fact  ... modified "2003-02-15" xmls:date,h2:date


add-fact  ... issued IssueDate "2003-02-15" xmls:date,h2:data
add-fact  ... modified ModifiedDate "2003-02-15" xmls:date,h2:date

add-fact  ... issued IssueDate "2003-02-15" xmls:date,h2:data
add-fact  ... modified ModifiedDate "2003-02-15" xmls:date,h2:date

add-fact ... rdfs:comment "A summary of the resource."@en
add-fact ... rdfs:label "Access Rights"@en

 */
class FactParserOrmTest {

    /**
     * TODO Signature: Entity type name
     * Entity Type name: Country
     */

    /**
     * TODO Signature: Value type name
     * Value Type name: CountryCode
     *  anything that has no key is a value
     */

    /**
     * TODO Signature: Predicate name
     * ?speaks?veryWell
     * ?played?for?
     */

    /**
     * TODO Signature: Role name
     * RoleNaming(smokes.1, smokes.isSmoker)
     */

    /**
     * Unary fact type:
     * FactType(smokes (Person))
     *
     * Person as-isSmoker smokes:boolean
     * Person 'John','Lee Hooker' as-isSmoker smokes
     * ... has Name schema:name h2:varchar(100)
     * ... has Surname schema:surname h2:varchar(10)
     */
    @Test
    fun test_unary_fact() {
        // given
        val command = "add-fact Person smokes"
        val lastSubject = ""
        val changeSet = ChangeSet()
        val database = Database()
        // when
        val result = FactParser.parseFact(command, lastSubject, changeSet, database)
        // then
        assertEquals("", result.failure)
        assertEquals(1, database.schema.tables().size)
        val table = database.schema.table("Person")
        assertNotNull(table)
        assertEquals(2, table.columns.size)
        assertEquals("smokes", table.columns[1].name)
        assertEquals("Person", result.payload)
        val relation = database.schema.table("Person")!!
        assertEquals(
            " Person 'Giovanni' smokes 'true'",
            FactPrinter.print(
                relation, mapOf(
                    "smokes" to "true",
                    "Person" to "Giovanni",
                )
            )
        )
        assertEquals(
            " Person 'Giovanni' smokes 'true'",
            FactPrinter.print(
                relation, listOf(
                    "Giovanni",
                    "true",
                )
            )
        )
    }

    /**
     * Binary fact types:
     * FactType(wasBornIn (Person Country))
     *
     * Person was-born-in Country
     * Company as-employer employs Person as-employee
     */
    @Test
    fun test_binary_fact_type() {
        // given
        val command = "add-fact Person smokes Cigar"
        val lastSubject = ""
        val changeSet = ChangeSet()
        val database = Database()
        // when
        val result = FactParser.parseFact(command, lastSubject, changeSet, database)
        // then
        assertNotEquals("", result.message)
        assertEquals("", result.failure)
        assertEquals(3, database.schema.tables().size)
        val tablePerson = database.schema.table("Person")
        assertNotNull(tablePerson)
        val tableCigar = database.schema.table("Cigar")
        assertNotNull(tableCigar)
        assertEquals(1, tablePerson.columns.size)
        assertEquals("Person", tablePerson.columns[0].name)
        assertEquals("Person", result.payload)
        assertEquals(" Person 'null'", FactPrinter.print(tablePerson, mapOf()))
        val relation = database.schema.relation("smokes")!!
        assertEquals(
            " smokes '1' Person 'Giovanni' smokes Cigar 'true'",
            FactPrinter.print(
                relation, mapOf(
                    "smokes" to "1",
                    "Person" to "Giovanni",
                    "Cigar" to "true"
                )
            )
        )

    }

    /**
     * Ternary fact types:
     * FactType(?played?for? (Person Sport Country))
     *
     * Person as-player played Sport for Country
     *
     * Person played Sport
     * played has Person as Player
     * played for Country
     *
     * Person introduced Person to Person
     *
     * Person introduced Person
     * introduced has to Person
     *
     * Cat ate Food on Date
     *
     * Cat ate Food
     * ate has on Date
     *
     */
    @Test
    fun test_ternary_fact_type() {
        // given
        val lastSubject = ""
        val changeSet = ChangeSet()
        val database = Database()
        // when
        FactParser.parseFact("add-fact Person played Sport", lastSubject, changeSet, database)
        val result = FactParser.parseFact("add-fact played for Country", lastSubject, changeSet, database)
        // then
        val table = database.schema.relation("played")!!
        assertEquals(
            """
            has:
            played 
            Person 
            Sport role [played] 
            Country role [for] 
            
        """.trimIndent(), FactDescriptor.describeTable(table)
        )
        assertEquals(
            " played '1' Person 'Giovanni' played Sport 'Football' for Country 'Italy'",
            FactPrinter.print(
                table, mapOf(
                    "played" to "1",
                    "Person" to "Giovanni",
                    "Sport" to "Football",
                    "Country" to "Italy"
                )
            )
        )
        assertNotEquals("", result.message)
        assertEquals("", result.failure)
    }

    /**
     * Quaternary fact types:
     * FactType(?in?on?ate? (Person City Date Food))
     *
     * Person in City on Date ate Food
     *
     * Person in City
     * in has on Date
     * in has ate Food
     *
     */
    @Test
    fun test_quaternary_fact_types() {
        // given
        val lastSubject = ""
        val changeSet = ChangeSet()
        val database = Database()
        // when
        FactParser.parseFact("add-fact Person ate Food", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact ate in City", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact ate on Date", lastSubject, changeSet, database)
        // then
        val table = database.schema.relation("ate")!!
        assertEquals(
            " ate '1' Person 'Giovanni' ate Food 'Spaghetti' in City 'Milan' on Date '22-08-2023'",
            FactPrinter.print(
                table, mapOf(
                    "ate" to "1",
                    "Person" to "Giovanni",
                    "City" to "Milan",
                    "Date" to "22-08-2023",
                    "Food" to "Spaghetti"
                )
            )
        )
    }

    /**
     * Objectification:
     * FactType(enrolledIn (Student Course))
     * Objectifies(Enrolment enrolledIn)
     * FactType(resultedIn (Enrolment Grade))
     *
     * Student enrolled-in Course
     * enrolled-in as Enrolment
     * Enrolment resulted-in Grade
     *   =  enrolled-in resulted-in Grade
     */
    @Test
    fun test_objectification() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Student enrolled-in Course", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact enrolled-in as Enrolment", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Enrolment resulted-in Grade ", lastSubject, changeSet, database)
        // then
        assertEquals("Enrolment", database.schema.relation("Enrolment")!!.view)
        assertEquals("Enrolment", database.schema.relation("enrolled-in")!!.view)
        val enrolment = database.schema.table("Enrolment")
        assertEquals("[enrolledIn, Student, Course, Grade]", enrolment!!.columns.toString())
        assertEquals("Student_enrolled-in_Course", enrolment.name)
        val resultedIn = database.schema.relation("enrolled-in")
        assertEquals("[enrolledIn, Student, Course, Grade]", resultedIn!!.columns.toString())
        assertEquals("Student_enrolled-in_Course", resultedIn.name)
        assertEquals(
            " enrolledIn '1' Student 'Giovanni' enrolled-in Course 'Math' resulted-in Grade '10'",
            FactPrinter.print(
                enrolment, mapOf(
                    "enrolledIn" to "1",
                    "Course" to "Math",
                    "Student" to "Giovanni",
                    "Grade" to "10",
                )
            )
        )

    }

    /**
     * Unique(wasBornIn.1)
     *
     * N:1
     * Person was-born-in Country
     * Person has Country N:1 as:homeland
     */
    @Test
    fun test_unique_binary_fact_type_country() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Student was-born-in Country", lastSubject, changeSet, database)
        // when
        FactParser.parseFact(
            "add-fact was-born-in has Country at-most-one as:homeland",
            lastSubject,
            changeSet,
            database
        )
        FactParser.parseFact("add-fact was-born-in has Student some as:citizen", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("was-born-in")!!
        assertEquals("0..1", relation.columns.filter { it.name == "Country" }.first().cardinality)
        assertEquals("0..N", relation.columns.filter { it.name == "Student" }.first().cardinality)
        assertEquals(
            " wasBornIn '1' citizen Student 'Giovanni' homeland Country 'Italy'",
            FactPrinter.print(
                relation, mapOf(
                    "wasBornIn" to "1",
                    "Country" to "Italy",
                    "Student" to "Giovanni"
                )
            )
        )
    }

    @Test
    fun test_unique_binary_fact_type_country_prefix() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact some Student was-born-in at-most-one Country", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact was-born-in has Country as:homeland", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact was-born-in has Student as:citizen", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("was-born-in")!!
        assertEquals("0..1", relation.columns.filter { it.name == "Country" }.first().cardinality)
        assertEquals("0..N", relation.columns.filter { it.name == "Student" }.first().cardinality)
        assertEquals(
            " wasBornIn '1' citizen Student 'Giovanni' homeland Country 'Italy'",
            FactPrinter.print(
                relation, mapOf(
                    "wasBornIn" to "1",
                    "Country" to "Italy",
                    "Student" to "Giovanni"
                )
            )
        )
    }

    /**
     * Unique(isPresidentOf.1)
     * Unique(isPresidentOf.2)
     *
     * 1:1
     * Person is-president-of Country
     * is-president-of has Person as:[role] 1:1
     * is-president-of has Country as:[role] 1:1
     */
    @Test
    fun test_unique_binary_fact_type_president() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person is-president-of Country", lastSubject, changeSet, database)
        // when
        FactParser.parseFact(
            "add-fact is-president-of has Country exactly-one as:governs",
            lastSubject,
            changeSet,
            database
        )
        FactParser.parseFact(
            "add-fact is-president-of has Person exactly-one as:president",
            lastSubject,
            changeSet,
            database
        )
        // then
        val relation = database.schema.relation("is-president-of")!!
        assertEquals("1", relation.columns.filter { it.name == "Country" }.first().cardinality)
        assertEquals("1", relation.columns.filter { it.name == "Person" }.first().cardinality)
        assertEquals("governs", relation.columns.filter { it.name == "Country" }.first().role)
        assertEquals("president", relation.columns.filter { it.name == "Person" }.first().role)
        assertEquals(
            " isPresidentOf '1' president Person 'Giovanni' governs Country 'Italy'",
            FactPrinter.print(
                relation, mapOf(
                    "isPresidentOf" to "1",
                    "Country" to "Italy",
                    "Person" to "Giovanni"
                )
            )
        )
    }

    @Test
    fun test_unique_binary_fact_type_president_prefix() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact(
            "add-fact exactly-one Person is-president-of exactly-one Country",
            lastSubject,
            changeSet,
            database
        )
        // when
        FactParser.parseFact("add-fact is-president-of has Country as:governs", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact is-president-of has Person as:president", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("is-president-of")!!
        assertEquals("1", relation.columns.filter { it.name == "Country" }.first().cardinality)
        assertEquals("1", relation.columns.filter { it.name == "Person" }.first().cardinality)
        assertEquals("governs", relation.columns.filter { it.name == "Country" }.first().role)
        assertEquals("president", relation.columns.filter { it.name == "Person" }.first().role)
        assertEquals(
            " isPresidentOf '1' president Person 'Giovanni' governs Country 'Italy'",
            FactPrinter.print(
                relation, mapOf(
                    "isPresidentOf" to "1",
                    "Country" to "Italy",
                    "Person" to "Giovanni"
                )
            )
        )
    }

    /**
     * Unique(speaks.1 speaks.2)
     *
     * M:N
     * Person speaks Language
     * speaks has Person,Language M:N
     */
    @Test
    fun test_unique_binary_fact_type_languages() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person speaks Language", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact speaks has Person some as:speaker", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact speaks has Language,Person at-most-one", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("speaks")!!
        assertEquals("0..N", relation.columns.filter { it.name == "Person" }.first().cardinality)
        assertEquals(null, relation.columns.filter { it.name == "Language" }.first().cardinality)
        val constraint = database.schema.constraintsByType(Constraint.TYPE.UNIQUE)
        println(constraint)
        assertEquals("Language", constraint[0].source.columns[0].name)
        assertEquals("Person", constraint[0].source.columns[1].name)
    }

    /**
     * Unique on ternaries fact type:
     *
     * Unique(?got?in?.1 ?got?in?.3)
     * Unique(?got?in?.2 ?got?in?.3)
     *
     *
     * Team got Place in Competition
     * 1_3
     * got-in has Team,Competition at-most-one
     * 2_3
     * got-in has-at-most-one Place,Competition
     *
     */
    @Test
    fun test_unique_on_ternary_double_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Team got-place Place", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact got-place in Competition", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact got-place has Team,Competition at-most-one", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact got-place has Place,Competition at-most-one", lastSubject, changeSet, database)
        // then
        val constraintsByRelation = database.schema.constraintsByType(Constraint.TYPE.UNIQUE)
        //constraintsByRelation.map { println("${it.toString()}") }.joinToString(System.lineSeparator())
        assertEquals(2, constraintsByRelation.size)

        val firstConstraint = constraintsByRelation[0]
        assertEquals("UNIQUE", firstConstraint.type)
        assertEquals("Team_got-place_Place", firstConstraint.source.table)
        assertEquals(2, firstConstraint.source.columns.size)
        val firstNames = firstConstraint.source.columns.map { it.name }
        assertTrue(firstNames.contains("Team"))
        assertTrue(firstNames.contains("Competition"))
        assertEquals("", firstConstraint.target.table)

        val secondConstraint = constraintsByRelation[1]
        assertEquals("UNIQUE", secondConstraint.type)
        assertEquals("Team_got-place_Place", secondConstraint.source.table)
        assertEquals(2, secondConstraint.source.columns.size)
        val secondNames = secondConstraint.source.columns.map { it.name }
        assertTrue { secondNames.contains("Place") }
        assertTrue { secondNames.contains("Competition") }
        assertEquals("", secondConstraint.target.table)

        val relation = database.schema.relation("got-place")!!
        assertEquals(
            " gotPlace '1' Team 'F.T. Italy' got-place Place '5' in Competition 'Fencing'",
            FactPrinter.print(
                relation, mapOf(
                    "gotPlace" to "1",
                    "Team" to "F.T. Italy",
                    "Place" to "5",
                    "Competition" to "Fencing",
                )
            )
        )
    }

    /**
     * Unique on ternaries fact type:
     *
     * Unique(?played?for?.1 ?played?for?.2 ?played?for?.3)
     *
     *
     * 1_2_3
     * Person played Sport for Country
     * played-for has-one Person,Sport,Country
     */
    @Test
    fun test_unique_on_ternary_single_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person played Sport", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact played for Country", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact played has Person,Sport,Country at-most-one", lastSubject, changeSet, database)
        // then
        assertNotNull(database.schema.table("Person"))
        assertNotNull(database.schema.table("Sport"))
        assertNotNull(database.schema.table("Country"))
        assertNotNull(database.schema.relation("played"))
        val constraintsByRelation = database.schema.constraintsByType(Constraint.TYPE.UNIQUE)
        //constraintsByRelation.map { println("${it.toString()}") }.joinToString(System.lineSeparator())
        assertEquals(1, constraintsByRelation.size)

        val firstConstraint = constraintsByRelation[0]
        assertEquals("UNIQUE", firstConstraint.type)
        assertEquals("Person_played_Sport", firstConstraint.source.table)
        assertEquals(3, firstConstraint.source.columns.size)
        val firstNames = firstConstraint.source.columns.map { it.name }
        assertTrue(firstNames.contains("Person"))
        assertTrue(firstNames.contains("Country"))
        assertTrue(firstNames.contains("Sport"))
        assertEquals("", firstConstraint.target.table)
    }


    /**
     * Simple mandatory role constraint:
     * Mandatory(Person wasBornIn.1)
     *
     * Person was-born-in Country
     * was-born-in has Person at-least-one
     */
    @Test
    fun test_simple_mandatory_role_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person was-born-in Country", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact was-born-in has Person at-least-one", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("was-born-in")!!
        assertEquals("wasBornIn", relation.columns[0].name)
        assertEquals(null, relation.columns[0].cardinality)
        assertEquals("Person", relation.columns[1].name)
        assertEquals("1..N", relation.columns[1].cardinality)
        assertEquals("Country", relation.columns[2].name)
        assertEquals(null, relation.columns[2].cardinality)
    }

    @Test
    fun test_simple_mandatory_role_constraint_prefix() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact at-least-one Person was-born-in Country", lastSubject, changeSet, database)
        // then
        val relation = database.schema.relation("was-born-in")!!
        assertEquals("wasBornIn", relation.columns[0].name)
        assertEquals(null, relation.columns[0].cardinality)
        assertEquals("Person", relation.columns[1].name)
        assertEquals("1..N", relation.columns[1].cardinality)
        assertEquals("Country", relation.columns[2].name)
        assertEquals(null, relation.columns[2].cardinality)
    }

    /**
     *  Inclusive-or constraint:
     *
     *  Mandatory(Visitor hasPassport.1 hasDriverLicence.1)
     *
     *  Visitor has Passport
     *  Visitor has DriverLicense
     *  Visitor has Passport,DriverLicense or-at-least-one
     *
     */
    @Test
    fun test_inclusive_or_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Visitor has-passport Passport", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Visitor has-license DriverLicense", lastSubject, changeSet, database)
        assertNotNull(database.schema.table("Visitor"))
        assertNotNull(database.schema.table("Passport"))
        assertNotNull(database.schema.table("DriverLicense"))
        assertNotNull(database.schema.relation("has-passport"))
        assertNotNull(database.schema.relation("has-license"))
        // when
        FactParser.parseFact(
            "add-fact Visitor has has-passport,has-license or-at-least-one",
            lastSubject,
            changeSet,
            database
        )
        // then
        println(database.schema.table("Visitor"))
        println(database.schema.relation("has-passport"))
        val constraintsByRelation = database.schema.constraintsByType(Constraint.TYPE.OR_AT_LEAST_ONE)
        //constraintsByRelation.map { println("${it.toString()}") }.joinToString(System.lineSeparator())
        assertEquals(1, constraintsByRelation.size)

        val firstConstraint = constraintsByRelation[0]
        assertEquals("has-passport", firstConstraint.source.table)
        assertEquals(1, firstConstraint.source.columns.size)
        assertEquals("Visitor", firstConstraint.source.columns[0].name)
        assertEquals("has-license", firstConstraint.target.table)
        assertEquals(1, firstConstraint.target.columns.size)
        assertEquals("Visitor", firstConstraint.target.columns[0].name)
    }

    /**
     * Preferred internal unique (reference scheme):
     * Identification(Country has.1 (has.2))
     *
     * Country has-id CountryCode
     */
    @Test
    fun test_reference_scheme() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact Country has-id CountryCode", lastSubject, changeSet, database)
        // then
        println(database.schema.table("Country"))
        assertEquals(1, database.schema.tables().size)
        assertEquals(2, database.schema.table("Country")!!.columns.size)
        assertEquals("Country", database.schema.table("Country")!!.columns[0].name)
        assertEquals("CountryCode", database.schema.table("Country")!!.columns[1].name)
        assertEquals("CountryCode", database.schema.table("Country")!!.naturalKey)
        assertEquals(2, database.schema.constraints().size)
        assertEquals("KEY_Country_Country", database.schema.constraints()[0].name)
        assertEquals(Constraint.TYPE.PRIMARY_KEY.name, database.schema.constraints()[0].type)
        assertEquals("KEY_Country_CountryCode", database.schema.constraints()[1].name)
        assertEquals(Constraint.TYPE.ID_KEY.name, database.schema.constraints()[1].type)// should be id
    }

    /**
     * External unique with preference:
     * ExternalIdentification(State (hasStateCode.2 isIn.2))
     * ExternalUnique(hasStateName.2 isIn.2)
     *
     * State has-code StateCode
     * State is-in Country(code)
     * State has-name StateName
     * State has-key StateCode,Country(code)
     * State has Country(code),StateName at-most-one
     *
     */
    @Test
    fun test_external_unique() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact exactly-one State has StateCode", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact exactly-one State is-in-country Country", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Country has-id Code", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact exactly-one State has StateName", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact State has-key StateCode,is-in-country", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact State has StateName,is-in-country at-most-one", lastSubject, changeSet, database)
        // then
        val stateTable = database.schema.table("State")!!
        assertEquals(4, stateTable.columns.size)
        assertNotNull(stateTable.colByName("State"))
        assertNotNull(stateTable.colByName("StateCode"))
        assertNotNull(stateTable.colByName("is-in-country"))
        assertNotNull(stateTable.colByName("StateName"))
        val constrs = database.schema.constraints()
        println(constrs)
        assertEquals(7, constrs.size)

        assertEquals("CANDIDATE_KEY State:State --> State:State ; ",
            database.schema.constraintsByTable("State").filter { it.type == Constraint.TYPE.CANDIDATE_KEY.name }.first()
                .toString()
        )
        assertEquals("PRIMARY_KEY State:is-in-country,StateCode --> State:is-in-country,StateCode ; ",
            database.schema.constraintsByTable("State").filter { it.type == Constraint.TYPE.PRIMARY_KEY.name }.first()
                .toString()
        )
        assertEquals("FOREIGN_KEY State:State --> State_is-in-country_Country:State ; ",
            database.schema.constraintsByTable("State").filter { it.type == Constraint.TYPE.FOREIGN_KEY.name }.first()
                .toString()
        )
        assertEquals("UNIQUE State:is-in-country,StateName --> : ; ",
            database.schema.constraintsByTable("State").filter { it.type == Constraint.TYPE.UNIQUE.name }.first()
                .toString()
        )

        assertEquals("PRIMARY_KEY State_is-in-country_Country:isInCountry --> State_is-in-country_Country:isInCountry ; ",
            database.schema.constraintsByTable("State_is-in-country_Country")
                .filter { it.type == Constraint.TYPE.PRIMARY_KEY.name }.first().toString()
        )

        assertEquals("FOREIGN_KEY Country:Country --> State_is-in-country_Country:Country ; ",
            database.schema.constraintsByTable("Country").filter { it.type == Constraint.TYPE.FOREIGN_KEY.name }.first()
                .toString()
        )
        assertEquals("ID_KEY Country:Code --> Country:Code ; ",
            database.schema.constraintsByTable("Country").filter { it.type == Constraint.TYPE.ID_KEY.name }.first()
                .toString()
        )
    }

    /**
     * Object Type Value Constraint:
     * ValuesOf(GenderCode (M F))
     *
     * enumeration:
     *
     * Gender(code) may-be {'M,'F'}
     * Rating(nr) may-be {1,2,3}
     *
     * ranges:
     *
     * Rating may-be {1..7}
     * Grade may-be {'A'..'F'}
     * Age(year) may-be {0..}
     * NegativeInt may-be {..-1}
     * PassScore(%) may-be {50..100}
     * NegativeTemperature(Celsius) may-be {-273.15..0}
     * ExtremeTemperature(Celsius) may-be {-100..-20,40..100}
     * SqlChar may-be {'a'..'z','A'..'Z','0'..'9','_'}
     *
     */
    @Test
    fun test_object_type_value_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact Gender has code may-be:{'M','F'}", lastSubject, changeSet, database)
        // then
        assertEquals("{'M','F'}",database.schema.table("Gender")!!.colByName("code")!!.default)
    }

    /**
     * Role value constraint:
     * ValuesOf(has.2 (0 … 140))
     *
     * Person has-age Age
     * hasAge has Age may-be {0..140}
     * Age(year) may-be {0..}
     *
     */
    @Test
    fun test_role_value_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person has-age Age", lastSubject, changeSet, database)
        // when
        FactParser.parseFact("add-fact has-age has Age may-be:{0..140}", lastSubject, changeSet, database)
        // then
        println(database.schema.relation("has-age"))
        assertEquals("{0..140}",database.schema.relation("has-age")!!.colByName("Age")!!.default)
    }


    /**
     * Subset constraint:
     * Subset((smokes.1 isCancerProne.1))
     * Subset((?for?obtained?.1 enrolledIn.1)(?for?obtained?.2 enrolledIn.2))
     *
     * Person smokes
     * Person is-cancer-prone
     * is-cancer-prone has smokes subset
     *
     * Person enrolled-in Course
     * Person for Course obtained Grade
     * Person_enrolled-in_Course has Person_for_Course_obtained_ subset
     *
     */
    @Test
    fun test_subset_constraint1() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person smokes", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Person is-cancer-prone", lastSubject, changeSet, database)
        // when
        val result = FactParser.parseFact("add-fact smokes subset is-cancer-prone", lastSubject, changeSet, database)
        // then
        assertTrue(result.isOK())
        assertEquals("SUBSET smokes:smokes --> is-cancer-prone:smokes ; ",
            database.schema.constraintsByType(Constraint.TYPE.SUBSET).first()
                .toString()
        )
    }

    @Test
    fun test_subset_constraint2() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person enrolled-in Course", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Person for Course obtained Grade", lastSubject, changeSet, database)
        // when
        val result = FactParser.parseFact("add-fact enrolled-in subset for-obtained", lastSubject, changeSet, database)
        // then
        assertTrue(result.isOK())
        assertEquals("SUBSET Person_enrolled-in_Course:Course,enrolledIn,Person --> for-obtained:Course,enrolledIn,Person ; ",
            database.schema.constraintsByType(Constraint.TYPE.SUBSET).first()
                .toString()
        )
    }

    /**
     * Join subset constraint:
     * JoinPath(P (speaks.1 speaks.2)(isOftenUsedIn.1 isOftenUsedIn.2))
     * Subset((servesIn.1 P.1)(servesIn.2 P.2))
     *
     *  Advisor speaks Language
     *  Language is-often-used-in Country
     *  Advisor serves-in Country
     *  Advisor has-key nr
     *  Language has-key name
     *  Country has-key code
     *
     * contraint not mapped in KP
     *
     *  serves-in has Advisor subset Advisor speaks
     *  serves-in has Country subset Country is-often-used-in
     */
    @Test
    fun test_join_subset_constraint2() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""

        FactParser.parseFact("add-fact some Advisor speaks some Language", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact speaks has-unique Advisor,Language", lastSubject, changeSet, database)  // TODO unique couple constraint

        FactParser.parseFact("add-fact some Language is-often-used-in some Country", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact is-often-used-in has-unique Language,Country", lastSubject, changeSet, database)  // TODO unique couple constraint

        FactParser.parseFact("add-fact Advisor serves-in Country", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact serves-in has-unique Advisor", lastSubject, changeSet, database) // TODO unique constraint

        FactParser.parseFact("add-fact Language has-id Name", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Country has-id Code", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Advisor has-id Nr", lastSubject, changeSet, database)

        // when
        val result = FactParser.parseFact("add-fact serves-in:Advisor,Country subset Language:speaks,is-often-used-in", lastSubject, changeSet, database)

        // then
        assertTrue(result.isOK())
        println()
        assertEquals("SUBSET Advisor_serves-in_Country:Advisor,Country --> Language:speaks,is-often-used-in:Advisor,Country ; ",
            database.schema.constraintsByType(Constraint.TYPE.SUBSET).first()
                .toString()
        )

    }

    /**
     * Exclusion constraint:
     * Exclusive((isWidowed.1 isMarried.1))
     * Exclusive((reviewed.1 authored.1)(reviewed.2 authored.2))
     *
     * Person is-married
     * ... is-widowed
     * is-widowed or-exclusive is-married
     *
     * Person authored Book
     * ... reviewed Book
     * authored or-exclusive reviewed
     *
     */
    @Test
    fun test_single_role_relation_exclusion_constraint1() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Person is-married", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Person is-widowed", lastSubject, changeSet, database)
        // when
        val result = FactParser.parseFact("add-fact is-married excludes is-widowed", lastSubject, changeSet, database)
        // then
        assertTrue(result.isOK())
        assertEquals("DISJUNCTION is-married:is-married --> is-widowed:is-married ; ",
            database.schema.constraintsByType(Constraint.TYPE.DISJUNCTION).first()
                .toString()
        )
    }

    @Test
    fun test_multi_role_relation_exclusion_constraint2() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact some Person authored some Book", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact some Person reviewed some Book", lastSubject, changeSet, database)
        // when
        val result = FactParser.parseFact("add-fact authored excludes reviewed", lastSubject, changeSet, database)
        // then
        assertTrue(result.isOK())
        assertEquals("DISJUNCTION Person_authored_Book:authored,Book,Person --> reviewed:authored,Book,Person ; ",
            database.schema.constraintsByType(Constraint.TYPE.DISJUNCTION).first()
                .toString()
        )
    }

    /**
     * Equality constraint:
     * Equal((hasSystolic.1 hasDiasystolic.1))
     *
     * Patient has-systolic BloodPressure
     * ... has-diasystolic BloodPressure
     * has-systolic Patient equals has-diasystolic Patient
     *
     */
     @Test
     fun test_single_role_equality_constraint() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        FactParser.parseFact("add-fact Patient has-systolic BloodPressure", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Patient has-diasystolic BloodPressure", lastSubject, changeSet, database)
        // when
        val result = FactParser.parseFact(
            "add-fact has-systolic equals has-diasystolic Patient",
            lastSubject,
            changeSet,
            database
        )
        // then
        assertTrue(result.isOK())
        assertEquals("EQUALITY has-systolic:Patient --> has-diasystolic:Patient ; ",
            database.schema.constraintsByType(Constraint.TYPE.EQUALITY).first()
                .toString()
        )


     }

    /**
     * Subtyping:
     * Subtype(Lecturer Employee)
     * Subtype(Employee Person)
     * Subtype(Student Person)
     * Subtype(StudentEmployee Student)
     * Subtype(StudentEmployee Employee)
     *
     * Student is-a Person
     * Employee is-a Person
     * StudentEmployee is-a Student,Employee
     * Lecturer is-a Employee
     *
     */
    @Test
    fun test_subtyping() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact Person has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Student has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Employee has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Lecturer is-a Employee", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Employee is-a Person", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Student is-a Person", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact StudentEmployee is-a Student,Employee", lastSubject, changeSet, database)
        // then
//        println(database.schema.table("StudentEmployee"))
//        println(database.schema.table("Student"))
//        println(database.schema.table("Employee"))
//        println(database.schema.table("Person"))
//        println(database.schema.table("Lecturer"))
        assertEquals("Person", database.schema.parents("Student").joinToString(",") { it.name })
        assertEquals("Employee", database.schema.parents("Lecturer").joinToString(",") { it.name })
        assertEquals("Person", database.schema.parents("Employee").joinToString(",") { it.name })
        assertEquals("Employee,Student",database.schema.children("Person").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("", database.schema.parents("Person").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("Lecturer,StudentEmployee",database.schema.children("Employee").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("StudentEmployee", database.schema.children("Student").joinToString(",") { it.name })
        assertEquals("Employee,Student",database.schema.parents("StudentEmployee").sortedBy { it.name }.joinToString(",") { it.name })

    }

    /**
     * Subtyping constraints:
     * ExclusiveSubtypes((Dog Cat) Animal)
     * ExhaustiveSubtypes((Player Coach) TeamMember)
     * ExclusiveSubtypes((MalePerson FemalePerson) Person)
     * ExhaustiveSubtypes((MalePerson FemalePerson) Person)
     *
     * Animal is-exclusive Dog,Cat
     * TeamMember is-covered Player,Coach
     * Person is-partitioned Male,Female
     *
     */
    @Test
    fun test_subtyping_exclusive_constraints() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact Dog has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Cat has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Animal is-exclusively Dog,Cat", lastSubject, changeSet, database)
        // then
        assertEquals("Cat,Dog", database.schema.children("Animal").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("EXCLUSIVE", database.schema.table("Cat")?.condition)
        assertEquals("EXCLUSIVE", database.schema.table("Dog")?.condition)
    }

    @Test
    fun test_subtyping_cover_constraints() {
        // given
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact Player has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Coach has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact TeamMember is-covered Player,Coach", lastSubject, changeSet, database)
        // then
        assertEquals("Coach,Player", database.schema.children("TeamMember").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("COVER", database.schema.table("Coach")?.condition)
        assertEquals("COVER", database.schema.table("Player")?.condition)
    }

    @Test
    fun test_subtyping_partition_constraints() {
        val database = Database()
        val changeSet = ChangeSet()
        val lastSubject = ""
        // when
        FactParser.parseFact("add-fact MalePerson has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact FemalePerson has-id Nr", lastSubject, changeSet, database)
        FactParser.parseFact("add-fact Person is-partitioned MalePerson,FemalePerson", lastSubject, changeSet, database)
        // then
        assertEquals("FemalePerson,MalePerson", database.schema.children("Person").sortedBy { it.name }.joinToString(",") { it.name })
        assertEquals("PARTITION", database.schema.table("MalePerson")?.condition)
        assertEquals("PARTITION", database.schema.table("FemalePerson")?.condition)
    }

    /**
     * TODO Internal frequency constraint:
     * Frequency(isAMemberOf.2 (12))
     * Frequency(isOn.2 (4, 7))
     * Frequency(reviews.1 (..5))
     * Frequency(reviews.2 (2..))
     * Frequency(?in?hadStaffOf?in?.1 ?in?hadStaffOf?in?.2 (2))
     *
     * Person is-a-member-of Jury
     * is-a-member has Jury  (12)
     *
     * Expert is-on Panel | Panel includes Expert
     * is-on has Panel (4,7)
     *
     * Expert reviews Paper | Paper is-reviewed-by Expert
     * reviews has Expert (..5)
     * ... has Paper (2..)
     *
     * Department in Year had-staff-of Gender in Quantity
     * _in_had-staff-of_in_ has-frequency Department Year 2
     *
     */

    /**
     * TODO External frequency constraint:
     * ExternalFrequency(isBy.2 isIn.2 (..2))
     *
     * Enrolment is-by Student
     * ... is-in Course
     * is-by Student has-frequency is-in Course ..2
     *
     */

    /**
     * TODO Value-comparison constraints:
     * ≥(endedOn.2 startedOn.2)
     *
     * Project started-on Date
     * started-on has Date as startdate
     * Project ended-on Date
     * ended-on has Date as enddate
     * enddate >= stateddate
     *       > < <=
     *
     */

    /**
     * TODO Object cardinality constraint:
     * TypeCardinality(President (0, 1))
     *
     * President has-frequency (..1)
     * UN_SecurityCouncilMember has-frequency {0,5..15}
     *
     */

    /**
     * TODO Role cardinality constraint:
     * RoleCardinality(isThePresidentOf (0, 1))
     *
     * Politician is-the-president
     * is-the-president has-frequency (0, 1)
     *
     */

    /**
     * TODO Ring constraints:
     * LocallyReflexive(P.1 P.2)
     *
     * Irreflexive
     * Reflexive (locally)
     * Asymmetric
     * Symmetric
     * Antisymmetric
     * Intransitive
     * Transitive
     * Stongly Intransitive
     * Acyclic
     * Asymmetric + Intransitive ...
     *
     * Person works-with Person
     * works-with is-ring irreflexive, asymmetric
     *
     */

    /**
     * TODO Derivation Rules:
     * SubTypeRule(Smoker (Person ∧ smokes))
     *
     * Person has PersonName
     * Person smokes
     * Smoker is-a { Person & smokes }
     *
     * Person lives-in-state State
     * State has StateCode
     * State is-in Country(code)
     * Person lives-in-country Country
     * lives-in-country is-a xy { Person x and exists z lives-in-state xz and State z and is-in zy and Country y }
     *
     * Person can-speak Language
     * Person has-key nr
     * Language has-key name
     * Person can-write-in Language
     * Person can-communicate-in Language
     * can-communicate-in is-a xy { Person x and ( can-speak xy or can-write xy) and Language y }
     * Person can-fully-communicate-in Language
     * can-fully-communicate-in is-a xy { Person x and can-speak xy and can-write xy and Language y }
     *
     */

    // -------------- Scenarios

    /**
     * Facts Scenario Player:
     * Player smokes (boolean fact)
     * Player was-born-in Country
     * Player speaks Language very well
     * Player played Sport for Team
     * Player in Team on Period ate Opponent
     * Player reports-to / manages Manager
     * Player / employs Team
     */

    // -------------- Internals

    /*
        From Instance
        "Bob" is-born-on "the 4th of July 1990".
        BobType is-born-on the4thofJuly1990Type
        rename BobType as Student
        Student is-a schema:Person
     */

    /*
        From Instance table
        + "Bob" is-a Person

        SQL:
        = create table personTable(PersonType varchar)
        + insert values into personTable("Bob")

        + ("Bob","Marley") is-a Person
        =
        Person("Bob","Marley")

        Person("Bob","Marley") has born-date 06/02/1945
        ... contributed-develop "reggae-music-life-style"
        ... contributed-spread "reggae-music-life-style"
        ... internationalized-by "reggae-music-life-style"

        ... awarded-with "Jamaica Order of Merit" on "1 month after dead-date"
        ... ranked 19 on "Rolling Stone 100 best singer"
        ... ranked 11 on "Rolling Stone 100 best artist"

        Person has-average life-span 80
        Person has-deviance life-span 20

        LTN-expert: Person("Bob","Marley") has life-span ?
        30%

     */

    @Test
    fun test_parseAsTripletMap() {
        // given
        val cmd = ">+ uno due tre quattro"
        // when
        val triple = FactParser.parseAsTripletMap(cmd)
        // then
        assertNotNull(triple)
        assertEquals("uno", triple.get(FactParser.TripletRole.SUBJECT))
        assertEquals("due", triple.get(FactParser.TripletRole.PREDICATE))
        assertEquals("tre quattro", triple.get(FactParser.TripletRole.COBJECT))
        assertNull(triple.get(FactParser.TripletRole.COBJECTS))
    }

    @Test
    fun test_parseAsTripletMap_multi_cobj() {
        // given
        val cmd = ">+ uno due \"tre quattro\" cinque"
        // when
        val triple = FactParser.parseAsTripletMap(cmd)
        // then
        assertNotNull(triple)
        assertEquals("uno", triple.get(FactParser.TripletRole.SUBJECT))
        assertEquals("due", triple.get(FactParser.TripletRole.PREDICATE))
        assertEquals("\"tre quattro\"", triple.get(FactParser.TripletRole.COBJECT))
        //assertEquals("quattro", triple.get(FactParser.TripletRole.COBJECTS))
    }

    @Test
    fun test_parse_has_triplet_map() {
        // given
        val cmd = "add-fact Person has Name"
        // when
        val map = FactParser.parseAsTripletMap(cmd)
        // then
        assertNotNull(map)
        assertEquals(3,map.size)
        assertEquals(map[FactParser.TripletRole.SUBJECT],"Person")
        assertEquals(map[FactParser.TripletRole.PREDICATE],"has")
        assertEquals(map[FactParser.TripletRole.COBJECT],"Name")
    }

    @Test
    fun test_unary_fact_triplet() {
        // given
        val (subj,pred,_) = parseTriplet("add-fact Person smokes")
        assertEquals("Person",subj)
        assertEquals("smokes",pred)
        // when
        // then
    }

    @Test
    fun test_binary_relationship() {
        // given
        val (subj,pred,cobj)= parseTriplet("add-fact Person was-born-in Country")
        val db = Database()
        assertEquals(0,db.schema.tables?.size)
        // when
        val parseNaryRelationship = parseRelationship(db, subj, pred, cobj)
        // then
        assertTrue(parseNaryRelationship.isOK())
        assertEquals(2,db.schema.tables?.size)
        //println(XMLSerializerJacksonAdapter().prettyDatabase(db))
        assertNotNull(db.schema.table("Person_was-born-in_Country"))
        assertNotNull(db.schema.table("Country"))
    }

    @Test
    fun test_parse_has_cardinality_adding_col() {
        // given
        val database = Database()
        val cobjs = listOf("Phone","some")
        val sourceTable = Table()
        assertEquals(0,sourceTable.columns.size)
        assertFalse(sourceTable.hasColumn("Phone"))
        // when
        val result = FactParser.parseHas(cobjs, database, sourceTable)
        // then
        assertTrue(sourceTable.hasColumn("Phone"))
        assertEquals(1,sourceTable.columns.size)
        assertEquals("0..N",sourceTable.colByName("Phone")?.cardinality)
        assertFalse(result.message.isNullOrEmpty())
        // as there was no column creates a changeset
//        assertEquals(0,changeSet.commands?.size)
//        assertEquals(0,changeSet.createColumn?.size)
//        assertEquals("Phone",changeSet.createColumn[0].columns[0].name)
//        assertEquals("0..N",changeSet.createColumn[0].columns[0].cardinality)
    }

    @Test
    fun test_parse_has_cardinality_updating_col() {
        // given
        val database = Database()
        val sourceTable = Table() withColumn "Phone"
        assertEquals(1,sourceTable.columns.size)
        assertTrue(sourceTable.hasColumn("Phone"))
        // when
        val result = FactParser.parseHas(listOf("Phone", "some"), database, sourceTable)
        // then
        assertEquals(1,sourceTable.columns.size)
        assertEquals("0..N",sourceTable.colByName("Phone")?.cardinality)
        assertFalse(result.message.isNullOrEmpty())
    }


}