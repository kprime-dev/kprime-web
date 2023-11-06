package it.unibz.krdb.kprime.domain.cmd.argument

import it.unibz.krdb.kprime.domain.cmd.TraceCmd

class TraceCmdArgumentQName(name:String, description:String, min:Int = 0, max:Int = 0)
    : TraceCmdArgumentTextPattern(name,description, TraceCmd.QNAME_PATTERN, min,max)