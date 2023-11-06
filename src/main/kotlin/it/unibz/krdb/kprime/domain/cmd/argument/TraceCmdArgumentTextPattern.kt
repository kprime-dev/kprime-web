package it.unibz.krdb.kprime.domain.cmd.argument

open class TraceCmdArgumentTextPattern(name:String, description:String,val pattern:String, val min:Int = 0, val max:Int = 0)
    : TraceCmdArgument<String>(name,description) {

    override fun computeValidationErrors(value: Any?): List<String> {
        return when(value) {
            is String -> computeValidationErrors(value)
            else -> emptyList()
        }
    }

    fun computeValidationErrors(value: String): List<String> {
        val errors = ArrayList<String>()
        if (min>0 && value.length<min)  errors.add("$name[$value] size has to be at less $min chars.")
        if (max>0 && value.length>max)  errors.add("$name[$value] size has to be at most $max chars.")
        if (!(value.trim().isNotEmpty() && value.matches(pattern.toRegex()))) errors.add("$name[$value] has forbidden chars, required chars in pattern [$pattern] .")
        return errors
    }

    override fun fromString(value: String): String {
        return value
    }
}