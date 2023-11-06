package it.unibz.krdb.kprime.adapter

import com.fasterxml.jackson.module.kotlin.jsonMapper
import it.unibz.krdb.kprime.view.controller.ChartCircleController
import org.junit.Test
import kotlin.test.assertEquals

class CircleMapJsonTest {

    @Test
    fun test_simple_circle_map() {
        // given
        val circleEx = ChartCircleController.CircleTree.CircleNode("mio",
                mutableListOf(ChartCircleController.CircleTree.CircleLeaf("tuo",123))
        )
        // when
        val circleJson = jsonMapper().writeValueAsString(circleEx)
        // then
        assertEquals("""
            {"name":"mio","children":[{"name":"tuo","value":123,"type":""}]}
        """.trimIndent(),circleJson)
    }
}