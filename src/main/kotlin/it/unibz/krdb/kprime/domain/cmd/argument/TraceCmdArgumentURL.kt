package it.unibz.krdb.kprime.domain.cmd.argument

import it.unibz.krdb.kprime.domain.cmd.TraceCmd

class TraceCmdArgumentURL(name:String = "url_value", description:String = "URL value")
    : TraceCmdArgumentTextPattern(name,description, TraceCmd.URL_PATTERN)