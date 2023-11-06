package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test
import kotlin.test.assertEquals

class KotlinTest {

    @Test
    fun testExpertArgs() {
        // given
        val tokens = listOf("expert","pedia","search","aa","bb","cc","dd")
        val expertArgs = tokens.drop(3)
        var expertUrl = ""
        // when
        if (expertArgs.size % 2 == 0) {
            for (i in 0..expertArgs.size-1 step 2) {
                if (expertUrl.contains("?")) expertUrl += "&" else expertUrl += "?"
                expertUrl += "${expertArgs[i]}=${expertArgs[i+1]}"
            }
        }
        // then
        assertEquals("?aa=bb&cc=dd",expertUrl)


    }

}