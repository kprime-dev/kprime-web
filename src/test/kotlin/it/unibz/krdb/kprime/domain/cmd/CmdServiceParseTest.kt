package it.unibz.krdb.kprime.domain.cmd

import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

class CmdServiceParseTest {

    @Test
    fun test_parse_single_command() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >help
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(1, cmdEnvelope.cmdLines.size)
        assertEquals("help", cmdEnvelope.cmdLines[0])
        assertEquals("",cmdEnvelope.cmdContextId)
    }

    @Test
    fun test_parse_two_commands() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >help
            >add-table
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(1, cmdEnvelope.cmdLines.size)
        assertEquals("help add-table", cmdEnvelope.cmdLines[0])
        assertEquals("",cmdEnvelope.cmdContextId)
    }

    @Test
    fun `test parse two commands with comments`() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >help
            
            commento
            >add-table alfa
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(2, cmdEnvelope.cmdLines.size)
        assertEquals("help", cmdEnvelope.cmdLines[0])
        assertEquals("add-table alfa", cmdEnvelope.cmdLines[1])
        assertEquals("",cmdEnvelope.cmdContextId)
    }

    @Test

    fun test_parse_command_and_context_id() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >help#1234
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(1, cmdEnvelope.cmdLines.size)
        assertEquals("",cmdEnvelope.cmdContextId)
    }

    @Test
    fun `test parse two commands on two lines`() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >   select *             
            >     from table1
            >     where name!=null
            
            >add-table alfa
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(2, cmdEnvelope.cmdLines.size)
        assertEquals("select * from table1 where name!=null", cmdEnvelope.cmdLines[0])
        assertEquals("add-table alfa", cmdEnvelope.cmdLines[1])
        assertEquals("",cmdEnvelope.cmdContextId)
    }

    @Test
    fun `test parse two commands on two lines one with code`() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >   expert tpp             
            ```
            R:(ATT, REST)
            *MAPPINGS*
            S1 = PROJECTION[ATT](SELECTION[cond](R))
            S2 = PROJECTION[REST](SELECTION[cond](R))
            S3 = SELECTION[!cond](R)
            
            BREAK
            
            ---VALUES---
            
            <ID1,John>
            <ID2,Jane>
            <ID3,Jerd>
            
            BREAK
            ```
            
            >add-table alfa
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(2, cmdEnvelope.cmdLines.size)
        assertEquals("expert tpp", cmdEnvelope.cmdLines[0])
        assertEquals("add-table alfa", cmdEnvelope.cmdLines[1])
        assertEquals("",cmdEnvelope.cmdContextId)
        assertEquals("""
            R:(ATT, REST)
            *MAPPINGS*
            S1 = PROJECTION[ATT](SELECTION[cond](R))
            S2 = PROJECTION[REST](SELECTION[cond](R))
            S3 = SELECTION[!cond](R)
            
            BREAK
            
            ---VALUES---
            
            <ID1,John>
            <ID2,Jane>
            <ID3,Jerd>
            
            BREAK
        """.trimIndent(),cmdEnvelope.cmdPayload.joinToString("\n"))
    }

    @Test
    fun `test parse two commands on two lines one sql code`() {
        // given
        val cmdService = CmdServiceTest.getCmdService()
        val reqBody = """
            >   select             
            ```
            sum(hours)
            from
            (
              select sum(no_of_hours) as hours
              from ft_daily_monitoring ft, abs_daily_monitoring abs
              where ft.monitoring_id = abs.monitoring_date_id and project_id = 132
            union all
              select sum(man_hours)
              from
              (select units_completed, crew_size, hourly_pitch, (units_completed/hourly_pitch * crew_size) as man_hours
                  from weekly_monitoring wm, ft_pitch p
                  where wm.activity_id = p.activity_id and p.project_id = 132 and wm.project_id =132) wm) t;
            ```
            
            >add-table alfa
        """.trimIndent()
        // when
        val cmdEnvelope = CmdService.parseCmdEnvelop(reqBody)
        // then
        assertEquals(2, cmdEnvelope.cmdLines.size)
        assertEquals("select", cmdEnvelope.cmdLines[0])
        assertEquals("add-table alfa", cmdEnvelope.cmdLines[1])
        assertEquals("",cmdEnvelope.cmdContextId)
        assertEquals("""
            sum(hours)
            from
            (
            select sum(no_of_hours) as hours
            from ft_daily_monitoring ft, abs_daily_monitoring abs
            where ft.monitoring_id = abs.monitoring_date_id and project_id = 132
            union all
            select sum(man_hours)
            from
            (select units_completed, crew_size, hourly_pitch, (units_completed/hourly_pitch * crew_size) as man_hours
            from weekly_monitoring wm, ft_pitch p
            where wm.activity_id = p.activity_id and p.project_id = 132 and wm.project_id =132) wm) t;
        """.trimIndent(), cmdEnvelope.cmdPayload.joinToString("\n"))
    }

}