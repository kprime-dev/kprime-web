package it.unibz.krdb.kprime.domain.cmd.argument

class TraceCmdArgumentConstraint(name:String, description:String, min:Int = 0, max:Int = 0)
    : TraceCmdArgumentTextPattern(name,description,".*", min,max)