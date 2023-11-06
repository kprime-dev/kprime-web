package it.unibz.krdb.kprime.domain.cmd.argument

class TraceCmdArgumentPath(name:String, description:String, min:Int = 0, max:Int = 0)
    : TraceCmdArgumentTextPattern(name,description,"^[a-zA-Z0-9_///-]*$", min,max)