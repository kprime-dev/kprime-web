package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentInteger
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.cmd.create.TraceCmdAddGoal
import it.unibz.krdb.kprime.domain.cmd.read.TraceCmdMetaToDatabase
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TraceCmdTest {

    @Test
    fun test_validate_zero_tokens() {
        // given
        val cmdLineTokens = listOf<String>()
        val cmdArgs = listOf<TraceCmdArgumentI>()
        // when
        val validated = TraceCmd.validate(Pair(cmdLineTokens, emptyMap()), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("",validated.failure)
        assertEquals("",validated.warning)
        assertTrue(validated.isOK())
    }

    @Test
    fun test_validate_failure_one_unexpected_token() {
        // given
        val cmdLineTokens = listOf("ciccio unexpected-token")
        val cmdArgs = listOf<TraceCmdArgumentI>()
        // when
        val validated = TraceCmd.validate(Pair(cmdLineTokens, emptyMap()), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("",validated.failure)
        assertEquals("",validated.warning)
        assertTrue(validated.isOK())
    }

    @Test
    fun test_validate_one_token() {
        // given
        val cmdLineTokens = listOf("ciccio","franco")
        val cmdArgs = listOf<TraceCmdArgumentI>(
            TraceCmdArgumentText("name","name of the actor",3,20) required true
        )
        // when
        val validated = TraceCmd.validate(Pair(cmdLineTokens, emptyMap()), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("",validated.failure)
        assertEquals("",validated.warning)
        assertTrue(validated.isOK())
    }

    @Test
    fun test_validate_one_token_too_short() {
        // given
        val cmdLineTokens = listOf("name","ciccio")
        val cmdArgs = listOf<TraceCmdArgumentI>(
            TraceCmdArgumentText("name","name of the actor",10,20) required true
        )
        // when
        val validated = TraceCmd.validate(Pair(cmdLineTokens, emptyMap()), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("name[ciccio] size has to be at less 10 chars.; ",validated.failure)
        assertEquals("",validated.warning)
        assertFalse(validated.isOK())
    }

    @Test
    fun test_validate_one_token_mandatory_one_optional_ok() {
        // given
        val sqlCommandWithOptions = "alfa sourceA -TARGET_NAME=22222"
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        val cmdArgs = listOf(
            TraceCmdArgumentText("SOURCE_NAME","name of the context",3,40) required true,
            TraceCmdArgumentText("TARGET_NAME","name of the target",3,10) required false
        )
        // when
        val validated = TraceCmd.validate(Pair(mandatory, optionals), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("",validated.failure)
        assertEquals("",validated.warning)
        assertEquals("22222",validated.argsValues()["TARGET_NAME"])
        assertEquals("sourceA",validated.argsValues()["SOURCE_NAME"])
        assertTrue(validated.isOK())
    }

    @Test
    fun test_validate_one_token_mandatory_one_optional_integer_ok() {
        // given
        val sqlCommandWithOptions = "alfa sourceA -TARGET_NAME=9"
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        val cmdArgs = listOf(
            TraceCmdArgumentText("SOURCE_NAME","name of the context",3,40) required true,
            TraceCmdArgumentInteger("TARGET_NAME","name of the target",3,10) required false
        )
        // when
        val validated = TraceCmd.validate(Pair(mandatory, optionals), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("",validated.failure)
        assertEquals("",validated.warning)
        assertEquals(9,validated.argsValues()["TARGET_NAME"])
        assertEquals("sourceA",validated.argsValues()["SOURCE_NAME"])
        assertTrue(validated.isOK())
    }

    @Test
    fun test_validate_one_token_optional_too_short() {
        // given
        val sqlCommandWithOptions = "alfa sourceA -TARGET_NAME=22222"
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        val cmdArgs = listOf(
            TraceCmdArgumentText("SOURCE_NAME","name of the context",3,40) required true,
            TraceCmdArgumentText("TARGET_NAME","name of the target",3,4) required false
        )
        // when
        val validated = TraceCmd.validate(Pair(mandatory, optionals), cmdArgs)
        // then
        assertEquals("",validated.message)
        assertEquals("[TARGET_NAME[22222] size has to be at most 4 chars.];",validated.failure.trim())
        assertEquals("",validated.warning)
        assertFalse(validated.isOK())

    }

    @Test
    fun test_separate_optionals() {
        // given
        val sqlCommandWithOptions = "alfa -beta=22"
        // when
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        // then
        assertEquals("alfa",mandatory.joinToString(","))
        assertEquals("22",optionals["beta"])
    }

    @Test
    fun test_separate_optionals_with_envelope() {
        // given
        val sqlCommandWithOptions = """
            >add-mapping q1 -source=confu
            ```
            select * 
            from ft_daily_monitoring
            ```
        """.trimIndent()
        // when
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        // then
        assertEquals(">add-mapping,q1,select * \n" +
                "from ft_daily_monitoring",mandatory.joinToString(","))
        assertEquals("confu",optionals["source"])
    }

    @Test
    fun test_separate_optionals_in_long_cmd() {
        // given
        val sqlCommandWithOptions = "alfa gamma delta -beta=22 rufus"
        // when
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        // then
        assertEquals("alfa,gamma,delta,rufus",mandatory.joinToString(","))
        assertEquals("22",optionals["beta"])
    }

    @Test
    fun test_separate_optionals_in_long_cmd_with_much_spaces() {
        // given
        val sqlCommandWithOptions = "alfa gamma    delta -beta=22 rufus"
        // when
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        // then
        assertEquals("alfa,gamma,delta,rufus",mandatory.joinToString(","))
        assertEquals("22",optionals["beta"])
    }

    @Test
    fun test_separate_optionals_wrong_no_value() {
        // given
        val sqlCommandWithOptions = "alfa -beta"
        // when
        val (mandatory, optionals) = TraceCmd.separateArgsOptionals(sqlCommandWithOptions)
        // then
        assertEquals("alfa,-beta",mandatory.joinToString(","))
        assertEquals(null,optionals["beta"])
    }

    @Test
    fun test_is_free_text() {
        // given
        val mandartories = listOf(
            TraceCmdArgumentText("ArgNames.GOAL_TITLE.name", "Goal small description.") required true,
            TraceCmdArgumentFreeText("ArgNames.GOAL_TITLE.name", "Goal small description.") required true
        )
        // then
        assertFalse(TraceCmd.noFreeText(mandartories))
        // given
        val mandartories2 = listOf(
            TraceCmdArgumentText("ArgNames.GOAL_TITLE.name", "Goal small description.") required true
        )
        // then
        assertTrue(TraceCmd.noFreeText(mandartories2))
    }

    @Test
    fun test_validate_mandatories() {
        // given
        val args = listOf(
            TraceCmdArgumentFreeText("GOAL_TITLE", "Goal small description.") required true
        )
        val tokens = "add-goal testo libero".split(" ").drop(1)
        // when
        val values = HashMap<String,Any?>()
        TraceCmd.validateMandatories(args,values,tokens)
        // then
        println(values)
        assertEquals("testo libero",values["GOAL_TITLE"])
    }
}
