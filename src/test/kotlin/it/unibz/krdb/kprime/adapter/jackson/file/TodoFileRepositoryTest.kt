package it.unibz.krdb.kprime.adapter.jackson.file

import it.unibz.krdb.kprime.adapter.JsonFileContextRepositoryTest
import it.unibz.krdb.kprime.domain.todo.Todo
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class TodoFileRepositoryTest {

    @Test
    fun test_add_one_goal_to_repo() {
        // given
        val contextDir = this.javaClass.getResource(".")!!.toExternalForm().drop(5)
        val repo = TodoFileRepositoryBuilder().build(contextDir)
        repo.deleteAll()
        assertEquals(0,repo.countAll())
        val goal =  Todo()
        //when
        repo.save(goal)
        // then
        assertEquals(1,repo.countAll())
    }

    @Test
    fun test_delete_one_goal_to_repo() {
        // given
        val contextDir = this.javaClass.getResource(".")!!.toExternalForm().drop(5)
        val repo = TodoFileRepositoryBuilder().build(contextDir)
        repo.deleteAll()
        assertEquals(0,repo.countAll())
        val goal =  Todo()
        repo.save(goal)
        assertEquals(1,repo.countAll())
        //when
        repo.delete(goal)
        // then
        assertEquals(0,repo.countAll())
    }

    @Test
    fun test_delete_one_goal_pos_0_to_repo() {
        // given
        val contextDir = this.javaClass.getResource(".")!!.toExternalForm().drop(5)
        val repo = TodoFileRepositoryBuilder().build(contextDir)
        repo.deleteAll()
        assertEquals(0,repo.countAll())
        val goal =  Todo()
        repo.save(goal)
        assertEquals(1,repo.countAll())
        //when
        val goalFound = repo.findAll()[0]
        val goalDeleted = repo.delete(goalFound)
        // then
        assertEquals(0,repo.countAll())
        assertTrue(goalDeleted)
    }

    @Test
    fun test_todo_repo_delete_criteria() {
        // given
        val repoDir = JsonFileContextRepositoryTest::class.java.getResource("/repo/actors/actors.json")
        val repoTodo = TodoFileRepositoryBuilder().build(repoDir.file)
        repoTodo.deleteAll()
        repoTodo.save(Todo(id=2,title="goal2"))
        repoTodo.save(Todo(id=5,title="goal5"))
        repoTodo.save(Todo(id=6,title="goal6"))
        val listBefore = repoTodo.findAll()
        kotlin.test.assertEquals(3, listBefore.size)
        repoTodo.delete { it.id == 5L }
        val listAfter = repoTodo.findAll()
        kotlin.test.assertEquals(2, listAfter.size)
        kotlin.test.assertEquals(2L, listAfter[0].id)
        kotlin.test.assertEquals(6L, listAfter[1].id)
    }


}