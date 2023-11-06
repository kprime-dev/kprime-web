package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.domain.db.Constraint

object TraceCmdListForeigns : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "foreigns"
    }

    override fun getCmdDescription(): String {
        return "List foriegn keys from current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "read,logical,constraint"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val database = context.env.database
        val constraints = database.schema.constraints().filter { c -> c.type.equals(Constraint.TYPE.FOREIGN_KEY.name) }
        var result = "Foreign Keys:"+System.lineSeparator()
        for (constraint in constraints) {
            result += " ${constraint.name} of type ${constraint.type}"+System.lineSeparator()
        }
        return TraceCmdResult() message result
    }

}
