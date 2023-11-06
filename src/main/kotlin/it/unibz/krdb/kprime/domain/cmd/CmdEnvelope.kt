package it.unibz.krdb.kprime.domain.cmd

data class CmdEnvelope (val cmdLines:List<String>,
                        val cmdContextId:String,
                        val cmdPayload:List<String>)