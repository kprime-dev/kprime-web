package it.unibz.krdb.kprime.domain

import it.unibz.krdb.kprime.domain.cmd.CommandRequestEvent
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class CommandRequestEventTest {

    @Test
    fun test_request_event_to_from_log_string() {
        // given
        val time = LocalDateTime.now()
        val event = CommandRequestEvent(time,"nico pedot","add-table xyz")
        // when
        val asLog = event.toLog()
        // then
        assertEquals("(request):${time.format(CommandRequestEvent.dateTimeFormatter)}_nico pedot> add-table xyz oid=''",asLog)

        // when
        val toEvent = CommandRequestEvent.fromLog(asLog)
        // then
        assertEquals(event.toString(),toEvent.toString())
    }
}