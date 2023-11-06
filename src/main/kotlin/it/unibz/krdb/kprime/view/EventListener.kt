package it.unibz.krdb.kprime.view

interface EventListener {
    fun eventListen(docId:String,event:String)
}