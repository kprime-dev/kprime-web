package it.unibz.krdb.kprime.generic.script

import org.junit.Ignore
import org.junit.Test
import javax.script.ScriptEngineManager
import kotlin.test.assertEquals

/*
https://github.com/Kotlin/kotlin-script-examples/blob/master/jvm/jsr223/jsr223-main-kts/src/test/kotlin/org/jetbrains/kotlin/
https://www.programcreek.com/java-api-examples/?api=javax.script.ScriptEngineManager
 */
class ScriptTest {

    @Test
    @Ignore
    fun testSimpleEval() {
        val engine = ScriptEngineManager().getEngineByExtension("kts")!!
        val res = engine.eval("2+4")
        assertEquals(6,res)
    }

    @Test
    @Ignore
    fun testSimpleBinding() {
        val engine = ScriptEngineManager().getEngineByExtension("kts")!!
        engine.put("hello","world")
        val res = engine.eval("hello")
        assertEquals("world",res)
    }

}