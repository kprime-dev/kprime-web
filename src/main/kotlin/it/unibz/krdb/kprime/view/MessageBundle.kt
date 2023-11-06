package it.unibz.krdb.kprime.view

import java.io.InputStreamReader
import java.text.MessageFormat
import java.util.*

class MessageBundle(var languageTag: String?) {
    private val messages: ResourceBundle
    operator fun get(message: String?): String {
        if (languageTag!="en" && languageTag!="zh") languageTag="en"
        val stream = MessageBundle::javaClass.javaClass.getResourceAsStream("/localization/messages_${languageTag}.properties")
        val bundle = PropertyResourceBundle(InputStreamReader(stream, "UTF-8"))
        return bundle.getString(message)
    }

    operator fun get(key: String?, vararg args: Any?): String {
        return MessageFormat.format(get(key), *args)
    }

    init {
        val locale = languageTag?.let { Locale(it) } ?: Locale.ENGLISH
        messages = ResourceBundle.getBundle("localization/messages", locale)
    }
}