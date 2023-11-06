package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentConstraint
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI

object TraceCmdAddDoubleInc : TraceCmd {
    override fun getCmdName(): String {
        return "add-double-inc"
    }

    override fun getCmdDescription(): String {
        return "Adds double inclusione constraint to relational model."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.LOGICAL,
            TraceCmd.Topic.CONSTRAINT,
            TraceCmd.Topic.DATABASE).joinToString()
    }

    internal enum class ArgNames {
        CONSTRAINT_LINE;
    }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentConstraint(ArgNames.CONSTRAINT_LINE.name, "Constraint as: <source-tab-name>:<attr-names>--><target-tab-name>:<attr-names>") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val constraintLine = args[ArgNames.CONSTRAINT_LINE.name] as String
        val database = context.env.database
        database.schema.addDoubleInc(constraintLine)
        return TraceCmdResult() message "Add double inclusion executed."
    }

}
