package it.unibz.krdb.kprime.adapter.jackson

import it.unibz.krdb.kprime.domain.todo.Todo
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class JacksonServiceTest {

    @Test
    @Ignore
    fun test_to_json_goal() {
        // given
        val goal  = Todo()
        val jacksonService = JacksonService()
        // when
       val result = jacksonService.toJson(goal)
        // then
        assertEquals("""
            {
              "id" : -1,
              "title" : "",
              "completed" : false,
              "hidden" : false,
              "key" : "",
              "dateCreated" : "2023-08-04T12:19:19.982Z",
              "priority" : 0,
              "estimate" : 1,
              "partof" : "",
              "assignee" : "",
              "isOpened" : false,
              "isClosed" : false,
              "gid" : "299f4981-01de-4336-ac76-dc88b3066dc1",
              "labels" : ""
            }            
        """.trimIndent(),result)
    }
}