package it.unibz.krdb.kprime.domain.cmd.argument

interface TraceCmdArgumentI {
    fun computeValidationErrors(value: Any?): List<String>
    fun fromString(value: String):Any
    fun getArgName():String
    fun isRequired(): Boolean
}

abstract class TraceCmdArgument<T>(var name: String,var description: String): TraceCmdArgumentI {
    var required: Boolean = false
    var default: Any? = null
    var classe: Any = String.javaClass

    override fun isRequired(): Boolean {
        return required
    }
    infix fun required(required : Boolean): TraceCmdArgument<T> {
        this.required = required
        return this
    }

    infix fun default(default: Any): TraceCmdArgument<T> {
        this.default = default
        return this
    }

    override fun toString():String {
        return "${this.name} : ${this.description} default:${this.default} required:${this.required}"
    }

    override fun getArgName(): String {
        return name
    }
}