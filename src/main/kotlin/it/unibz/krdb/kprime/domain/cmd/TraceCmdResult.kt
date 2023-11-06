package it.unibz.krdb.kprime.domain.cmd

import it.unibz.krdb.kprime.domain.trace.TraceName

@Suppress("UNCHECKED_CAST")
class TraceCmdResult() {
    var message:String = ""
    var messageType:String? = null
    var failure:String = ""
    var warning:String = ""
    var currentTrace: TraceName = TraceName("")
    var currentTraceFile:String = ""
    var payload:Any? = null
    var payloadType:String? = null
    var options = emptyList<String>()
    var oid:String= ""

    infix fun message(message: String ) = apply {
        this.message = message
    }

    infix fun messageType(messageType: String ) = apply {
        this.messageType = messageType
    }

    infix fun failure(message: String ) = apply {
        this.failure = message
    }

    infix fun warning(message: String ) = apply {
        this.warning = message
    }

    infix fun trace(currentTrace: TraceName) = apply {
        this.currentTrace = currentTrace
    }

    infix fun file(currentTraceFile: String) = apply {
        this.currentTraceFile = currentTraceFile
    }

    infix fun options(options: List<String>)  = apply {
        this.options = options
    }

    infix fun payload(payload:Any) = apply {
        this.payload = payload
    }

    infix fun payloadType(payloadType:String) = apply {
        this.payloadType = payloadType
    }

    infix fun oid(newOid:String) = apply {
        this.oid = newOid
    }

    fun isOK():Boolean {
        return this.failure.isEmpty()
    }

    internal fun argsValues(): Map<String, Any> {
        return this.payload as Map<String,Any>
    }

}