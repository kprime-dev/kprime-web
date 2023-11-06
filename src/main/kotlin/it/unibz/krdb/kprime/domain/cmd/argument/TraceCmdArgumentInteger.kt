package it.unibz.krdb.kprime.domain.cmd.argument

class TraceCmdArgumentInteger(name:String, description: String = "", val min:Int=-9999, val max:Int=9999)
    : TraceCmdArgument<Int>(name,description) {
    override fun computeValidationErrors(value: Any?): List<String> {
        return when (value) {
            is Int -> computeValidationErrors(value)
            is String -> computeValidationErrors(value)
            else -> emptyList()
        }
    }

    fun computeValidationErrors(value: Int): List<String> {
        val errors = ArrayList<String>()
        if (value<min)  errors.add("$name[$value] has to be more than $min")
        if (value>max)  errors.add("$name[$value] has to be less than $max")
        return errors
    }

    fun computeValidationErrors(value: String): List<String> {
        return try {
            computeValidationErrors(fromString(value))
        } catch (e:Exception) {
            listOf("Value $name[$value] is required as integer.")
        }
    }

    override fun fromString(value: String): Int {
        return Integer.valueOf(value)
    }

}