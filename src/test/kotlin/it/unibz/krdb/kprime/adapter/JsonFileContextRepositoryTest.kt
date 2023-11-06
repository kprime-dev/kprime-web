package it.unibz.krdb.kprime.adapter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.unibz.krdb.kprime.domain.actor.Actor
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class JsonFileContextRepositoryTest {

    @Test
    fun test_jackson_actors() {
        // given
        val repoDir = JsonFileContextRepositoryTest::class.java.getResource("/repo/actors/actors.json")!!
            .path.substringBeforeLast("actors/")
        val repoFileName ="actors/actors.json"
        // when
        val readValue = jacksonObjectMapper()
            .readValue(
                File(repoDir + repoFileName).readText(Charsets.UTF_8),
                object: TypeReference<List<Actor>>() {}
            )
        println(readValue)
        // then
        assertEquals(1L,readValue[0].id)
    }

}