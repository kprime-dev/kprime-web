package it.unibz.krdb.kprime.adapter

import it.unibz.krdb.kprime.adapter.jackson.file.ActorFileRepository
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ActorFileRepositoryTest {

    @Test
    fun test_load_actors() {
        // given
        val repoDir =
            ActorFileRepositoryTest::class.java.getResource("/repo/actors/actors.json")!!
                .path.substringBeforeLast("actors/")
        // when
        val findAll = ActorFileRepository().build(repoDir)
            .findAll()
        // then
        assertEquals(3,findAll.size)
        assertEquals("email1",findAll[0].email)
        assertEquals("email2",findAll[1].email)
    }

    @Test
    fun test_load_actor_memberOf1() {
        // given
        val repoDir =
            ActorFileRepositoryTest::class.java.getResource("/repo/actors/actors.json")!!
                .path.substringBeforeLast("actors/")
        // when
        val findAll = ActorFileRepository().build(repoDir)
            .findByCriteria { it.memberOf == "memberOf1" }
        // then
        assertEquals(2,findAll.size)
    }

    @Test
    fun test_load_actor_ID2() {
        // given
        val repoDir =
            ActorFileRepositoryTest::class.java.getResource("/repo/actors/actors.json")!!
                .path.substringBeforeLast("actors/")
        // when
        val findAll = ActorFileRepository().build(repoDir)
            .findFirstBy { it.id == 1L }
        // then
        assertNotNull(findAll)
    }

}