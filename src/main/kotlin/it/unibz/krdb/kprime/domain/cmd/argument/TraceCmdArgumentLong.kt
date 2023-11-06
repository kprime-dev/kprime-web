package it.unibz.krdb.kprime.domain.cmd.argument

class TraceCmdArgumentLong(name:String, description: String = "", val min:Int=-9999, val max:Int=9999)
    : TraceCmdArgument<Int>(name,description) {
    override fun computeValidationErrors(value: Any?): List<String> {
        return when (value) {
            is Long -> computeValidationErrorsLong(value)
            is String -> computeValidationErrorsString(value)
            else -> emptyList()
        }
    }

    private fun computeValidationErrorsLong(value: Long): List<String> {
        val errors = ArrayList<String>()
        if (value<min)  errors.add("$name[$value] has to be more than $min")
        if (value>max)  errors.add("$name[$value] has to be less than $max")
        return errors
    }

    private fun computeValidationErrorsString(value: String): List<String> {
        return try {
            computeValidationErrors(fromString(value))
        } catch (e:Exception) {
            listOf("Value $name[$value] is required as Long.")
        }
    }

    override fun fromString(value: String): Long {
        return value.toLong()
    }

}