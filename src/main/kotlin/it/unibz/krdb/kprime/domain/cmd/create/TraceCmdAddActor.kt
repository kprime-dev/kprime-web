package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.*
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText

object TraceCmdAddActor: TraceCmd {

    override fun getCmdName(): String {
        return "add-actor"
    }

    override fun getCmdDescription(): String {
        return "Adds one actor to current context."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.CONCEPTUAL,
            TraceCmd.Topic.ACTOR).joinToString()
    }

    private enum class ArgNames { NAME, member_of, ROLE, email, pass }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.NAME.name,"name of the actor",3,20) required true,
            TraceCmdArgumentText(ArgNames.ROLE.name,"role of the actor",3,20) required true,
            TraceCmdArgumentText(ArgNames.member_of.name,"member of the actor",3,20) required false,
            TraceCmdArgumentText(ArgNames.email.name,"email of the actor",3,50) required false,
            TraceCmdArgumentText(ArgNames.pass.name,"pass of the actor",3,50) required false
        )
    }

    override fun executeMap(context: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val name =  args[ArgNames.NAME.name]  as String
        val role =  args[ArgNames.ROLE.name]  as String
        val memberOf =  args[ArgNames.member_of.name]  as String? ?:""
        val email =  args[ArgNames.email.name]  as String? ?:""
        val pass =  args[ArgNames.pass.name]  as String? ?:""
        val prjContextLocation = context.env.prjContextLocation
        context.pool.actorService.addActor(prjContextLocation,
            name = name, role = role, memberOf =memberOf, pass=pass, mail=email).fold(
                onSuccess = { return TraceCmdResult() message "OK! Actor added with role:$role memberOf:$memberOf name:$name ."},
                onFailure = { return TraceCmdResult() failure "Can't create a new actor. ${it.message}" }
            )
    }


}