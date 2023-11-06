package it.unibz.krdb.kprime.domain.cmd.argument

open class TraceCmdArgumentBoolean(name:String, description:String)
    : TraceCmdArgument<Boolean>(name,description) {

    override fun computeValidationErrors(value: Any?): List<String> {
        return when(value) {
            is String -> computeValidationErrors(value)
            else -> emptyList()
        }
    }

    fun computeValidationErrors(value: String): List<String> {
        val errors = ArrayList<String>()
        if (value != "true" && value != "false")
            errors.add("$name requires value 'true' or 'false'.")
        return errors
    }

    override fun fromString(value: String): Boolean {
        return value == "true"
    }
}