package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.SchemaCmdParser

object TraceCmdCSAddMultivalued : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-cs-multivalued"
    }

    override fun getCmdDescription(): String {
        return "Add one multivalued constraint to current changeset."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <table-name>:<col-name>,...,<col-name>--><col-name>,...,<col-name>"
    }

    override fun getCmdTopics(): String {
        return "write,logical,constraint,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val changeSet = context.env.changeSet
        val constraintString = command.split(" ")[1]
        val tableName = constraintString.split(":")[0]
        val constrainSpec = constraintString.split(":")[1]
        val index = (context.env.database.schema.constraints?.size?: 0) + context.env.changeSet.size()
        val constraints = SchemaCmdParser.parseMultivalued(index,tableName,constrainSpec)
        changeSet plus constraints.first()
        return TraceCmdResult() message "Add multivalued constraint to changeset executed."
    }

}