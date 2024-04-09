package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.trace.TraceName

interface TraceCmd {

    enum class Topic { WRITE, READ, CHECK,
        DATABASE, TABLE, STORY, TERM, GOAL, MAPPING, SOURCE, DRIVER, CONSTRAINT, PROJECT, CONTEXT,
        ACTOR, CHANGESET, DATA, VOCABULARY, LABEL, SYSTEM,
        LOGICAL, CONCEPTUAL, PHYSICAL, STATISTICAL
    }

    fun getCmdName():String

    fun getCmdDescription():String

    fun getCmdUsage():String {
        val argsDescription = getCmdArguments()
                .joinToString(System.lineSeparator()) { it -> it.toString() }
        return getCmdName()+ " : " + getCmdDescription() + System.lineSeparator() + argsDescription
    }

    fun getCmdTopics():String { return "" }

    fun getCmdArguments(): List<TraceCmdArgumentI> { return emptyList()}

    fun execute(context: CmdContext, command:String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        return executeTokens(separateArgsOptionals(command), context)
    }

    fun getCmdSuggestions(): TraceCmdResult {
        return TraceCmdResult()
    }

    fun executeTokens(
        tokens: Pair<List<String>, Map<String, String>>,
        context: CmdContext
    ): TraceCmdResult {
        val validation = validate(tokens, getCmdArguments())
        if (!validation.isOK()) return validation
        val args = validation.argsValues()
        println("----------- executeTokens ${args.size}:")
        println(args)
        return executeMap(context, args)
    }

    fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult
        { return TraceCmdResult() message "Empty execution."}

    // fun asArgsTokens(line:String) = line.trim().split(" ").drop(1)

    companion object {

        //const val URL_PATTERN = """https?://[a-zA-Z0-9-.]+\.[a-zA-Z]{2,6}(/.*)?"""
        const val URL_PATTERN = """https?://[a-zA-Z0-9-./_:]*$"""
        const val QNAME_PATTERN = """^[a-zA-Z0-9_\":.-]*$"""

        fun validate(tokens: Pair<List<String>, Map<String, String>>, args: List<TraceCmdArgumentI>): TraceCmdResult {
            val mandatoryTokens = tokens.first.drop(1)
            println("mandatory:$mandatoryTokens")
            val optionalTokens = tokens.second
            println("optionals:$optionalTokens")

            val argsMandatory = args.filter { it.isRequired() }
            val argsOptional = args.filter { !it.isRequired() }

            // check mandatory counting.
            if (noFreeText(argsMandatory)) {
                val countMandatories = argsMandatory.size
                if (mandatoryTokens.size != countMandatories)
                    return TraceCmdResult() failure "Read ${args.size} arguments. Required $countMandatories but found ${mandatoryTokens.size}."
            }

            // validate mandatory values.
            val argsValues = HashMap<String,Any?>()
            var validationResult = validateMandatories(argsMandatory, argsValues, mandatoryTokens)
            if (validationResult.isNotEmpty()) return TraceCmdResult() failure  validationResult

            // validate optional values.
            validationResult = validateOptionals(argsOptional, optionalTokens, validationResult, argsValues)
            if (validationResult.isNotEmpty()) return TraceCmdResult() failure  validationResult

            return TraceCmdResult() payload argsValues
        }

        internal fun validateMandatories(
            argsMandatory: List<TraceCmdArgumentI>,
            argsValues: HashMap<String, Any?>,
            mandatoryTokens: List<String>,
        ): String {
            var validationResult =""
            for (i in argsMandatory.indices) {
                if (argsMandatory[i] is TraceCmdArgumentFreeText) {
                    argsValues[argsMandatory[i].getArgName()] = mandatoryTokens.drop(i).joinToString(" ")
                    val validationErrorMessages =
                        argsMandatory[i].computeValidationErrors(mandatoryTokens[i]).joinToString(" --- ")
                    if (validationErrorMessages.isNotEmpty()) {
                        validationResult += "$validationErrorMessages; "
                    }
                    break
                }
                val validationErrorMessages =
                    argsMandatory[i].computeValidationErrors(mandatoryTokens[i]).joinToString(" --- ")
                if (validationErrorMessages.isNotEmpty()) {
                    validationResult += "$validationErrorMessages; "
                } else argsValues[argsMandatory[i].getArgName()] = argsMandatory[i].fromString(mandatoryTokens[i])
            }
            return validationResult
        }

        internal fun validateOptionals(
            argsOptional: List<TraceCmdArgumentI>,
            optionalTokens: Map<String, String>,
            validationResult: String,
            argsValues: HashMap<String, Any?>
        ): String {
            var validationResult1 = validationResult
            for (optionalArg in argsOptional) {
                val optionalValue = optionalTokens[optionalArg.getArgName()]
                if (optionalValue != null) {
                    val validationErrorMessages = optionalArg.computeValidationErrors(optionalValue)
                    if (validationErrorMessages.isNotEmpty()) {
                        validationResult1 += "$validationErrorMessages; "
                    } else argsValues[optionalArg.getArgName()] = optionalArg.fromString(optionalValue)
                }
            }
            return validationResult1
        }

        internal fun noFreeText(argsMandatory: List<TraceCmdArgumentI>): Boolean {
            return argsMandatory.filterIsInstance<TraceCmdArgumentFreeText>().isEmpty()
        }

        fun traceAndFile(currentTrace: TraceName?, tokens: List<String>): Pair<TraceName?, String> {
            var tmpTrace = currentTrace
            val newDb: String
            if (tokens.size > 2) {
                tmpTrace = TraceName(tokens[1])
                newDb = tokens[2]
            } else {
                newDb = tokens[1]
            }
            return Pair(tmpTrace, newDb)
        }

        @Deprecated("Use TraceCmdArgumentI",
            ReplaceWith("TraceCmdArgumentText")
        )
        internal fun isValidArgument(arg:String):Boolean {
            return arg.trim().isNotEmpty() && arg.matches("^[a-zA-Z0-9_]*$".toRegex())
        }

        @Deprecated("Use TraceCmdArgumentI",
            ReplaceWith("TraceCmdArgumentTextPattern")
        )
        internal fun isValidArgument(arg:String,patten:String):Boolean =
                arg.trim().isNotEmpty() && arg.matches((patten).toRegex())

        internal fun separateArgsOptionals(commandLines: String): Pair<List<String>,Map<String,String>>  {
            val commandWithOptions =
                if (commandLines.contains("```"))  commandLines.substringBefore("\n")
                    else commandLines
            val tokens = commandWithOptions.split(" ").filter { it.isNotEmpty() }
            val optionals = tokens.filter { CmdOption.isOptionalValue(it) }.map { CmdOption(it) }
                .associate { it.key() to it.value() }
            val mandatory = tokens.filterNot { CmdOption.isOptionalValue(it) }.toMutableList()
            if (commandLines.contains("```")) {
                val linesAfterCommand = commandLines.substringAfter("\n")
                val payload = CmdService.parseCmdEnvelop(linesAfterCommand).cmdPayload
                println("--------------payload")
                println(payload)
                println("--------------payload END")
                if (payload.isNotEmpty())
                    mandatory.add(payload.joinToString(System.lineSeparator()))
            }
            return Pair(mandatory,optionals)
        }
    }

}

@JvmInline
value class CmdOption(val value:String) {
    companion object {
        fun isOptionalValue(value: String):Boolean {
            return value.startsWith("-") && value.contains("=")
        }

    }
 fun key():String {
    return value.substringAfter("-").substringBefore("=")
 }
    fun value():String {
        return value.substringAfter("=")
    }
}