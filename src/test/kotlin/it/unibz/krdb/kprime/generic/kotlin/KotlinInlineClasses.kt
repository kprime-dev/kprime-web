package it.unibz.krdb.kprime.generic.kotlin

import org.junit.Test

@JvmInline
value class IBAN(val iban:String)

@JvmInline
value class IBAN2(val iban:IBAN)

//class IBAN3 : IBAN("") {} This type is final, so it cannot be inherited from

typealias Money = Int

// Inline classes cannot extend other classes and are always final.
// they are allowed to declare properties and functions, and have the init block:
// They can only have simple computable properties (no lateinit/delegated properties).
// exprimental . Inline classes can also have a generic type parameter as the underlying type.
// Implementation by delegation to inlined value of inlined class is allowed with interfaces.

class KotlinInlineClasses {

    @Test
    fun test_typealias() {
        val income :Money = 123
        when(income) {
            is Int -> println("is int")
            is Money -> println("money")
            else -> println("unknown")
        }
    }

    @Test
    fun test_inlined_class() {
        val income = IBAN("123")
        when(income) {
            // is String -> println("is int") Incompatible types: String and IBAN
            is IBAN -> println("money")
            else -> println("unknown")
        }
    }

}