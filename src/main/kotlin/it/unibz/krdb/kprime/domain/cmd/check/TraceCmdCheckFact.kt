package it.unibz.krdb.kprime.domain.cmd.check

import it.unibz.krdb.kprime.adapter.fact.FactPrinter
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdCheckFact: TraceCmd {

    override fun getCmdName(): String {
        return "check-fact"
    }

    override fun getCmdDescription(): String {
        return "Checks one fact instance over one relation."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.CHECK,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.DATA).joinToString()
    }

    private enum class ArgNames { RELATION_NAME, instances }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.RELATION_NAME.name,"name of the relation",3,20) required true,
            TraceCmdArgumentFreeText(ArgNames.instances.name,"list of instances to check",3,20) required true,
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val relationName =  args[ArgNames.RELATION_NAME.name]  as String
        val roleInstances =  args[ArgNames.instances.name]  as String
        val db = context.env.database
        val relation = db.schema.relation(relationName)
            ?: return TraceCmdResult() failure "Relation [$relationName] not found."
        val instances = roleInstances.split(" ")
            .filter { it.startsWith("'") }
            .map { it.drop(1).dropLast(1)}
        println("TraceCmdCheckFact instances: $instances")
        val instancesApplied = FactPrinter.print(relation, instances).trim()
        return if (instancesApplied == roleInstances) TraceCmdResult() message "Checked."
        else TraceCmdResult() failure  """
             Not Checked 
             [$instancesApplied]
             [$roleInstances]
        """.trimIndent()
    }

}