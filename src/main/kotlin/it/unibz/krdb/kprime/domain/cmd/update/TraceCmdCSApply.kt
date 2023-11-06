package it.unibz.krdb.kprime.domain.cmd.update

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.common.ApplyChangeSetUseCase

object TraceCmdCSApply : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "changeset-apply"
    }

    override fun getCmdDescription(): String {
        return "Apply current changeset to current database."
    }

    override fun getCmdUsage(): String {
        return getCmdName()
    }

    override fun getCmdTopics(): String {
        return "write,logical,changeset"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val serializer = XMLSerializerJacksonAdapter()
        val applyChangeSetUseCase = ApplyChangeSetUseCase(serializer)
        val appliedDB = applyChangeSetUseCase.apply(context.env.database, context.env.changeSet)
        context.env.database = appliedDB
        return TraceCmdResult() message serializer.prettyDatabase(appliedDB)
    }

}
