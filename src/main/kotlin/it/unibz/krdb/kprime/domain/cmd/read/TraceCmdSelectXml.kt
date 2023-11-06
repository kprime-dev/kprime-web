package it.unibz.krdb.kprime.domain.cmd.read

import it.unibz.krdb.kprime.domain.cmd.CmdContext
import it.unibz.krdb.kprime.domain.cmd.TraceCmdResult
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object TraceCmdSelectXml : it.unibz.krdb.kprime.domain.cmd.TraceCmd {
    override fun getCmdName(): String {
        return "select-xml"
    }

    override fun getCmdDescription(): String {
        return "Select from current database XML one xpath tokens."
    }

    override fun getCmdUsage(): String {
        return getCmdName() + " <xpath>"
    }

    override fun getCmdTopics(): String {
        return "read,logical,xml"
    }

    override fun execute(context: CmdContext, command: String): TraceCmdResult {
        if (command.endsWith("??")) return TraceCmdResult() message getCmdUsage()
        if (command.endsWith("?")) return TraceCmdResult() message getCmdDescription()
        val datasourcePath: String = context.getCurrentTraceFilePath()
        val commandWithoutPrefix = command.split(" ").drop(1).joinToString(" ")
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(File(datasourcePath))
        val xpath = XPathFactory.newInstance().newXPath()
        val result = xpath.compile(commandWithoutPrefix).evaluate(doc, XPathConstants.NODESET) as NodeList
        val resultMessage= asValueList(result).joinToString(",")
        return TraceCmdResult() message resultMessage
    }

    private fun asValueList(xpathResultNodes: NodeList): MutableList<String> {
        val listNodeValues = mutableListOf<String>()
        for (nodeId in 0..xpathResultNodes.length) {
            val item = xpathResultNodes.item(nodeId)
            if (item==null) continue
            listNodeValues.add("nodename="+item.nodeName)
            if (item.nodeValue==null) continue
            listNodeValues.add("nodevalue="+item.nodeValue)
        }
        return listNodeValues
    }

}


