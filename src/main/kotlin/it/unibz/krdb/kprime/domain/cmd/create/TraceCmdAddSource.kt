package it.unibz.krdb.kprime.domain.cmd.create

import it.unibz.krdb.kprime.domain.source.Source
import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentFreeText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentI
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentText
import it.unibz.krdb.kprime.domain.cmd.argument.TraceCmdArgumentTextPattern

object TraceCmdAddSource: it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "add-source"
    }

    override fun getCmdDescription(): String {
        return "Creates a new H2 source."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.WRITE,
            TraceCmd.Topic.SOURCE,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    private enum class ArgNames { SOURCE_NAME, DRIVER_CLASS, SOURCE_USER, SOURCE_PASS, SOURCE_LOCATION }

    override fun getCmdArguments(): List<TraceCmdArgumentI> {
        return listOf(
            TraceCmdArgumentText(ArgNames.SOURCE_NAME.name,"name of the source",3,20) required true,
            TraceCmdArgumentTextPattern(ArgNames.DRIVER_CLASS.name,"driver class name (E.g. org.h2.Driver)","^[a-zA-Z0-9_/./-]*$", 3,200) required true,
            TraceCmdArgumentText(ArgNames.SOURCE_USER.name,"source user name (E.g. sa)",2,20) required true,
            TraceCmdArgumentText(ArgNames.SOURCE_PASS.name,"source user pass (E.g. -)",3,20) required false,
            TraceCmdArgumentTextPattern(ArgNames.SOURCE_LOCATION.name,"source location  (E.g. jdbc:h2:file:~/data/mydata.db)","^[a-zA-Z0-9_////~/./-/:]*$", 3,2000) required true,
        )
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = cmdContext.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val contextName = cmdContext.env.prjContextName.value

        val newSourceName =  args[ArgNames.SOURCE_NAME.name]  as String
        val newSourceDriver =  args[ArgNames.DRIVER_CLASS.name]  as String
        val newSourceUser =  args[ArgNames.SOURCE_USER.name]  as String
        val newSourceLocation =  args[ArgNames.SOURCE_LOCATION.name]  as String
        val newSourcePass =  args[ArgNames.SOURCE_PASS.name]  as String? ?:""

        val source = Source(
            id = newSourceName,
            type = "jdbc",
            name = newSourceName,
            driver = newSourceDriver, //"org.h2.Driver",
            location = newSourceLocation, //"jdbc:h2:file:~/data/${newSourceName}.db",
            user = newSourceUser, //"sa",
            pass = newSourcePass) //""
        cmdContext.pool.sourceService.addContextSource(contextName, source)


        return TraceCmdResult() message "Source created."
    }
}