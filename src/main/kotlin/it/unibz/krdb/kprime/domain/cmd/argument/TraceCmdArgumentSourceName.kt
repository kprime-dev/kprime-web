package it.unibz.krdb.kprime.domain.cmd.argument

class TraceCmdArgumentSourceName(name:String = "source_name", description:String = "Data source name")
    : TraceCmdArgumentText(name,description,3,50)