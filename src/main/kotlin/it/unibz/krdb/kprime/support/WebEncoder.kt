package it.unibz.krdb.kprime.support

import java.net.URLDecoder
import java.net.URLEncoder

class WebEncoder {

    companion object {
         fun encode(urlToEncode:String):String {
            return URLEncoder.encode(urlToEncode,"UTF-8")
        }

        fun decode(urlToDecode:String):String {
            return URLDecoder.decode(urlToDecode,"UTF-8")
        }
    }
}