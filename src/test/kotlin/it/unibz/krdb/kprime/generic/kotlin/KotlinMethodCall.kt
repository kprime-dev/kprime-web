package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test

class KotlinMethodCall {

    object Mary {
        fun `check that the price of #pet is #price`(pet: String, price: Int): String {
            println(pet)
            println(price)
            return "ok"
        }
    }

    @Test
    fun test_method_call_with_params() {
        Mary.`check that the price of #pet is #price`(pet = "lamb", price = 64)
    }
}