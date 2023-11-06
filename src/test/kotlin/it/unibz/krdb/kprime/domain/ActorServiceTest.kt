package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.adapter.jackson.file.PrjContextFileRepository
import it.unibz.krdb.kprime.domain.actor.ActorService
import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.project.PrjContextService
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.mock.ActorRepositoryMock
import it.unibz.krdb.kprime.mock.SettingRepositoryMock
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ActorServiceTest {

    lateinit var actorService: ActorService

    @Before
    fun before() {
        actorService = ActorService(
            prjContextService = PrjContextService(SettingService(SettingRepositoryMock()), PrjContextFileRepository()),
            actorRepositoryBuilder = ActorRepositoryMock()
        )
    }

    @Test
    fun test_list_actors_error_empty_location() {
        // given actorService
        // when
        val result = actorService.allActors("")
        // then
        assertEquals(true,result.isFailure)
        assertEquals(true,result.exceptionOrNull() is ActorService.Error.LocationEmpty)
        // or
        actorService.allActors("")
            .onSuccess {  assertTrue(false) }
            .onFailure { it is ActorService.Error.LocationEmpty }
    }

    @Test
    fun test_replace_all_actors_with_wrong_context_name() {
        // given actorService
        // when
        val result = actorService.replaceAllActors("contextName", emptyList())
        // then
        assertEquals(true, result.isFailure)
        result.onFailure {
            when (it) {
                is ActorService.Error.ProjectUnknown -> { assertTrue(true) }
                is ActorService.Error.ContextNameEmpty -> { assertTrue(false) }
                else ->  { assertTrue(false) }
            }
        }
    }

    @Test
    fun test_chain_call() {
        actorService.allActors("")
        .onFailure { when(it) {
            is ActorService.Error.ProjectUnknown -> {}
            }
        }
        .onSuccess { actorService.addActor(PrjContextLocation("contextName"),
            "name","role","mem","pass","mail")
            .onFailure { when(it) {
                is ActorService.Error.ProjectUnknown -> {}
                }
        }

        }

    }
}