package it.unibz.krdb.kprime.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.unibz.krdb.kprime.adapter.jackson.JacksonProject
import it.unibz.krdb.kprime.domain.project.PrjContext
import org.junit.Test
import kotlin.test.assertEquals

class PrjContextJsonTest {

    @Test
    fun test_json_of_list_of_projects() {
        // given
        val gid = "nextGid()"
        val project = JacksonProject("myname", "mylocation", "mydesc", "mypicurl", "myactiveTrace", "myactiveTermBase", gid)
        project.resetLabels("label1")
        val listOfProjects = listOf(project)
        // when
        val json = jacksonObjectMapper().writeValueAsString(listOfProjects)
        // then
        val result = "[{\"name\":\"myname\",\"location\":\"mylocation\",\"description\":\"mydesc\",\"picUrl\":\"mypicurl\",\"activeTrace\":\"myactiveTrace\",\"activeTermBase\":\"myactiveTermBase\",\"gid\":\"nextGid()\",\"partOf\":\"\",\"license\":\"\",\"licenseUrl\":\"\",\"termsOfServiceUrl\":\"\",\"steward\":\"\",\"id\":0,\"labels\":\"label1\"}]".trimIndent()
        assertEquals(result, json)
    }

    @Test
    fun test_project_from_json() {
        // given
        val json = """
    [{
        "name": "alpinebits_destinations",
        "location": "/home/nipe/Workspaces/semint-kprime-cases/alpinebits/destinations/",
        "description": "",
        "picUrl": "https://www.alpinebits.org/wp-content/uploads/2015/10/alpine_bits_rgb.png",
        "activeTrace": "",
        "activeTermBase": "",
        "gid": "8220c598-68c1-4496-a2b6-0638f62ca544",
        "labels": "alpinebits,destinations",
        "partOf": "e5e6bbeb-c11b-4817-9165-a24e2cb5edfe",
        "license": "",
        "licenseUrl": ""
    }]
        """.trimIndent()
        // when
        val prjContexts = jacksonObjectMapper().readValue<List<PrjContext>>(json)
        // then
        assertEquals(1, prjContexts.size)
        assertEquals("alpinebits,destinations", prjContexts[0].labels)

    }
}