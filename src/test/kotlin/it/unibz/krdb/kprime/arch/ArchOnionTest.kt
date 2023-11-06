package it.unibz.krdb.kprime.arch

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures
import org.junit.Ignore
import org.junit.Test

class ArchOnionTest {

    @Test
    @Ignore
    fun test_services_in_package() {
       // given
        val importedClasses = ClassFileImporter().importPackages("it.unibz.krdb")
        val rule : ArchRule = classes().that().haveNameMatching("Repository")
            .should().resideInAPackage("..Project..")
        // then
        rule.check(importedClasses)
    }

    @Test
    fun test_onion() {
        Architectures.onionArchitecture()
            .domainModels("..domain..")
            .applicationServices("..services..")
            .adapter("Adapter","..adapter..")
    }
}