package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.cmd.CommandRequestEvent
import it.unibz.krdb.kprime.domain.cmd.CommandResponseEvent
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class CommandResponseEventTest {

    @Test
    fun test_request_event_to_from_log_string() {
        // given
        val time = LocalDateTime.now()
        val event = CommandResponseEvent(time,"nico pedot","add-table xyz","succ1","warn1","err1","oid1")
        // when
        val asLog = event.toLog()
        // then
        assertEquals("(response):${time.format(CommandRequestEvent.dateTimeFormatter)}_nico pedot> add-table xyz result='succ1' warning='warn1' error='err1' oid='oid1'",asLog)

        // when
        val toEvent = CommandResponseEvent.fromLog(asLog)
        // then
        assertEquals(event.toString(),toEvent.toString())
    }
}