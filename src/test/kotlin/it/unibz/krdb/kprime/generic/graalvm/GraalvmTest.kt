package it.unibz.krdb.kprime.generic.graalvm

import kotlin.test.Test

import org.graalvm.polyglot.*;

class GraalvmTest {

    @Test
    fun test_hello() {
        // given
        //val js_code = "(function myFun(param,limit){var sum = param+param; console.log('hello '+ (sum > limit));})"
        //val js_code = "(function myFun(){console.log('hello ');})"
        val js_code = "(function myFun(){ return 'hello 33'; })"


        // when
        try {
            Context.create().use { context ->
                val value: Value = context.eval("js", js_code)
                println(value.execute(22, 4555).asString())
            }
        } catch (e:Exception) {e.printStackTrace()}
    }
}