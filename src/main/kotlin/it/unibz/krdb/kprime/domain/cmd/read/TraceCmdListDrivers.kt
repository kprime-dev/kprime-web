package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmd
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import it.unibz.krdb.kprime.domain.source.Driver

object TraceCmdListDrivers : TraceCmd {
    override fun getCmdName(): String {
        return "drivers"
    }

    override fun getCmdDescription(): String {
        return "List drivers."
    }

    override fun getCmdTopics(): String {
        return listOf(
            TraceCmd.Topic.READ,
            TraceCmd.Topic.DRIVER,
            TraceCmd.Topic.PHYSICAL).joinToString()
    }

    override fun executeMap(cmdContext: CmdContext, args: Map<String, Any>): TraceCmdResult {
        val prjContextLocation = cmdContext.env.prjContextLocation
        if (prjContextLocation.isEmpty()) return TraceCmdResult() failure "No current context location."
        val instanceDrivers = cmdContext.pool.sourceService.readAllInstanceDriversPlusH2()
        val contextDrivers = emptyList<Driver>()
        return TraceCmdResult()  message successMessage(instanceDrivers,contextDrivers)
    }

    private fun successMessage(instanceDrivers: List<Driver>, contextDrivers: List<Driver>): String {
        var result = "Drivers:"+System.lineSeparator()
        for (driver in instanceDrivers) { result += "(I${driver.id}) ${driver.name}: ${driver.type} ${driver.className} ${driver.jarLocation}" + System.lineSeparator() }
        for (driver in contextDrivers)  { result += "(C${driver.id}) ${driver.name}: ${driver.type} ${driver.className} ${driver.jarLocation}" + System.lineSeparator() }
        return result
    }


}
