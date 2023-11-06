package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentConstraint
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdAddDoubleFd : TraceCmd {
    override fun getCmdName(): String {
        return "add-double-fd"
    }

    override fun getCmdDescription(): String {
        return "Adds a bidirectional functional dependency constraint to relational model."
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
            TraceCmdArgumentConstraint(ArgNames.CONSTRAINT_LINE.name, "Constraint as: <source-tab-name>:<attr-names>--><attr-names>") required true
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val constraintLine = args[ArgNames.CONSTRAINT_LINE.name] as String
        val database = context.env.database
        val tableName = constraintLine.substringBefore(":")
        val setExpression = constraintLine.substringAfter(":")
        val setConstraints = SchemaCmdParser.parseFunctionals(1, tableName, setExpression)
        database.schema.constraints().addAll(setConstraints)
        val setReversedConstraints = setConstraints.map { Constraint.of(it.right(),it.left(), Constraint.TYPE.FUNCTIONAL.name) }
        database.schema.constraints().addAll(setReversedConstraints)
        return TraceCmdResult() message "Add double functional executed."
    }

}
