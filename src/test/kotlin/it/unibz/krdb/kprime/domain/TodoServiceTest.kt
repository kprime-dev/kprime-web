package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.project.PrjContextLocation
import it.unibz.krdb.kprime.domain.setting.SettingService
import it.unibz.krdb.kprime.domain.todo.Todo
import it.unibz.krdb.kprime.domain.todo.TodoService
import it.unibz.krdb.kprime.mock.SettingRepositoryMock
import it.unibz.krdb.kprime.mock.TodoRepositoryBuilderMock
import it.unibz.krdb.kprime.support.TimeSupport
import org.junit.Test
import kotlin.test.assertEquals

class TodoServiceTest {

    @Test
    fun test_goal_status() {
        // given
        val todoRepoBuilder = TodoRepositoryBuilderMock()
        val todoRepo = todoRepoBuilder.build("")
        val goals = listOf(
            Todo(id=1, dateDue = TimeSupport.toDate("06-01-2021")),
            Todo(id=2, dateDue = TimeSupport.toDate("01-01-2024")),
            Todo(id=3, dateDue = TimeSupport.toDate("03-08-2023")),
            Todo(id=4, dateDue = TimeSupport.toDate("06-08-2023"))
        )
        todoRepo.saveAll(goals)
        val todoService = TodoService(SettingService(SettingRepositoryMock()), todoRepoBuilder)
        // when
        val status :String  = todoService.status(PrjContextLocation(""),TimeSupport.toDate("04-08-2023"))
        // then
        assertEquals("""
            Goal 1 is EXPIRED from P-2Y-6M-29D.
            Goal 3 is EXPIRED from P-1D.
            Goal 4 is in 7 days ALERT ZONE P2D.
            
        """.trimIndent(),status)
    }

    @Test
    fun test_goal_status_fine() {
        // given
        val todoRepoBuilder = TodoRepositoryBuilderMock()
        val todoRepo = todoRepoBuilder.build("")
        val goals = listOf(
            Todo(id=2, dateDue = TimeSupport.toDate("01-01-2024")),
        )
        todoRepo.saveAll(goals)
        val todoService = TodoService(SettingService(SettingRepositoryMock()), todoRepoBuilder)
        // when
        val status :String  = todoService.status(PrjContextLocation(""),TimeSupport.toDate("04-08-2023"))
        // then
        assertEquals("""
            All 1 goals are fine.
        """.trimIndent(),status)
    }

    @Test
    fun test_goal_status_0_goals() {
        // given
        val todoRepoBuilder = TodoRepositoryBuilderMock()
        val todoRepo = todoRepoBuilder.build("")
        val goals = emptyList<Todo>()
        todoRepo.saveAll(goals)
        val todoService = TodoService(SettingService(SettingRepositoryMock()), todoRepoBuilder)
        // when
        val status :String  = todoService.status(PrjContextLocation(""),TimeSupport.toDate("04-08-2023"))
        // then
        assertEquals("""
            There are no goals.
        """.trimIndent(),status)
    }

}